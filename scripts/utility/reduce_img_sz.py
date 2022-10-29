from PIL import Image
from glob import glob
from tqdm import tqdm
import os


sdir = "src/main/resources/common/stamp/ae_en"
img_paths = glob(sdir + "/*.jpg")
ddir = "ar_en"
os.makedirs(ddir, exist_ok=True)
for spath in tqdm(img_paths):
    img = Image.open(spath)

    # img = img.resize(img.size, Image.ANTIALIAS)
    fname = spath.split("/")[-1]
    dpath = os.path.join(ddir, fname)
    img.save(dpath, optimize=True, quality=80)  # The saved downsized image size is 22.9kb
