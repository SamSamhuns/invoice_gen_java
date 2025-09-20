import os
import glob
import random
import shutil

ext = set([".png", ".jpg", ".jpeg"])

src = "English"
target = "multi_script_handwritten_signature"
dirlist = sorted(glob.glob(os.path.join(src, "*")))

os.makedirs(target, exist_ok=True)

for dir in dirlist:
    num = dir.split("/")[-1]
    flist = sorted(glob.glob(os.path.join(dir, "*")))
    flist = [p for p in flist if os.path.splitext(p)[1] in ext]
    flist = random.choices(flist, k=2)

    for fpath in flist:
        fname = fpath.split("/")[-1]
        tpath = os.path.join(target, f"{num}_{fname}")
        shutil.copy(fpath, tpath)
