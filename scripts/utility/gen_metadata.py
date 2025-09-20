import os
import json
import tqdm
import glob

ext = set([".png", ".jpg", ".jpeg"])

type = "signature"
type = "stamp"
type = "logo"


# for signatures
if type == "signature":
    src = "src/main/resources/common/signature/kaggle_real_forged"
    flist = sorted(glob.glob(os.path.join(src, "*")))
    arr = []
    for fpath in tqdm.tqdm(flist):
        fext = os.path.splitext(fpath)[1].lower()
        if fext in ext:
            name = fpath.split("/")[-1]
            # 171_01102011
            name = name[name.index("_") + 1 : name.index("_") + 6]
            dic = {"fullPath": "/".join(fpath.split("/")[-2:]), "name": name}
            arr.append(dic)
# for logos
if type == "logo":
    # logos need dominant color as well
    from colorthief import ColorThief

    src = "src/main/resources/common/logo/ae_en"
    flist = sorted(glob.glob(os.path.join(src, "*")))
    arr = []
    for fpath in tqdm.tqdm(flist):
        fext = os.path.splitext(fpath)[1].lower()
        if fext in ext:
            try:
                color_thief = ColorThief(fpath)
                dominant_color = color_thief.get_color(quality=1)
            except Exception as e:
                print(f"{e} for {fpath}")
                dominant_color = (0, 0, 0)  # if errors, use a black color
            name = fpath.split("/")[-1].split(".")[0]
            dic = {
                "fullPath": "/".join(fpath.split("/")[-2:]),
                "name": name,
                "color": dominant_color,
            }
            arr.append(dic)
# for stamp
if type == "stamp":
    src = "src/main/resources/common/stamp/ae_en"
    flist = sorted(glob.glob(os.path.join(src, "*")))
    arr = []
    for fpath in tqdm.tqdm(flist):
        fext = os.path.splitext(fpath)[1].lower()
        if fext in ext:
            name = fpath.split("/")[-1].split(".")[0]
            dic = {"fullPath": "/".join(fpath.split("/")[-2:]), "name": name}
            arr.append(dic)


with open("metadata.json", "w") as f:
    json.dump(arr, f, indent=4)
