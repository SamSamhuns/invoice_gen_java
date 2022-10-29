import numpy as np
import random
import shutil
import glob
import tqdm
import os
from PIL import Image


def get_bbox(img_ref, padding=2):
    bbox_probs = random.random()
    image = np.array(img_ref)
    image[image < 200] = 0
    black_pixels = np.array(np.where(image == 0))
    if bbox_probs <= 0.1:
        first_black_pixel = np.array(
            [min(black_pixels[0]), min(black_pixels[1])]) - random.randint(5, 35)
        last_black_pixel = np.array(
            [max(black_pixels[0]), max(black_pixels[1])]) + random.randint(5, 35)
    else:
        first_black_pixel = np.array(
            [min(black_pixels[0]), min(black_pixels[1])]) - padding
        last_black_pixel = np.array(
            [max(black_pixels[0]), max(black_pixels[1])]) + padding
    return first_black_pixel, last_black_pixel


rspath = "src/main/resources/common/logo/ae_en/**/*"
spaths = sorted(glob.glob(rspath, recursive=True))
ext_set = {".jpg", ".png", ".jpeg", ".webp"}
spaths = [p for p in spaths if os.path.splitext(p)[1] in ext_set]


for spath in tqdm.tqdm(spaths):
    sdir = "ae_en/" + spath.split('/')[-2]
    os.makedirs(sdir, exist_ok=True)
    tpath = os.path.join(sdir, spath.split('/')[-1])

    img_ref = Image.open(spath)

    first_black_pixel, last_black_pixel = get_bbox(img_ref)
    if any(np.concatenate((first_black_pixel, last_black_pixel)) < 0):
        print("inconsistent bounding boxes")
        shutil.copy(spath, tpath)
    else:
        img_cropped = img_ref.crop(
            (first_black_pixel[1], first_black_pixel[0], last_black_pixel[1], last_black_pixel[0]))
        img_cropped.save(tpath)
