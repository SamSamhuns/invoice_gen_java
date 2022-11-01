"""
Usage: python to_donut.py --dataset_dir /path/to/dataset
"""

import glob
import json
from pathlib import Path
from argparse import ArgumentParser

parser = ArgumentParser("Convert jsons to metadata jsonl")
parser.add_argument("--dataset_dir", dest="dataset_dir")
args = parser.parse_args()


def convert_to_donut():
    dataset_dir = args.dataset_dir
    json_paths = glob.glob(f"{dataset_dir}/*.json")
    img_paths = glob.glob(f"{dataset_dir}/*.jpg")
    assert len(json_paths) == len(
        img_paths), "Length of jsons and images do not match."
    jlines = []
    for i, json_path in enumerate(json_paths):
        with open(json_path, "r") as ojp:
            gt = json.load(ojp)
            gt = json.dumps(gt)
        img_name = Path(img_paths[i]).name
        jline = {}
        jline["file_name"] = img_name
        jline["ground_truth"] = {"gt_parse": gt}
        jlines.append(jline)
    with open(f"{dataset_dir}/metadata.jsonl", "w") as mj:
        for jline in jlines:
            mj.write(json.dumps(jline))
            mj.write("\n")


if __name__ == "__main__":
    convert_to_donut()
