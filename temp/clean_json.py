
import json

path = "/Users/sourabhsoni/Code/onePeace/one_piece_content/src/main/resources/assets/one_piece_content/animations/sand_spike.animation.json"

with open(path, "r", encoding='utf-8') as f:
    data = json.load(f)

count = 0
for anim in data.get("animations", {}).values():
    bones = anim.get("bones", {})
    for bone in bones.values():
        if "lerp_mode" in bone:
            del bone["lerp_mode"]
            count += 1

print(f"Removed 'lerp_mode' from {count} bones.")

with open(path, "w", encoding='utf-8') as f:
    json.dump(data, f, indent='\t')
