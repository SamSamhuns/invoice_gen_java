"""
Usage: python to_donut.py --dataset_dir /path/to/dataset
"""

import glob
import tqdm
import json
from pathlib import Path
from argparse import ArgumentParser


def convert_to_donut(dataset_dir):
    json_paths = sorted(glob.glob(f"{dataset_dir}/**/*.json", recursive=True))
    img_paths = sorted(glob.glob(f"{dataset_dir}/**/*.jpg", recursive=True))
    assert len(json_paths) == len(
        img_paths), "Length of jsons and images do not match."
    jlines = []
    for i, json_path in tqdm.tqdm(enumerate(json_paths)):
        with open(json_path, "r") as ojp:
            gt = json.load(ojp)
        img_name = Path(img_paths[i]).name
        jline = {}
        jline["file_name"] = img_name
        jline["ground_truth"] = json.dumps({"gt_parse": gt})
        jlines.append(jline)
    with open(f"{dataset_dir}/metadata.jsonl", "w") as mj:
        for jline in jlines:
            mj.write(json.dumps(jline))
            mj.write("\n")


if __name__ == "__main__":
    parser = ArgumentParser("Convert jsons to metadata jsonl")
    parser.add_argument("-d", "--dataset_dir", dest="dataset_dir")
    args = parser.parse_args()

    convert_to_donut(args.dataset_dir)
