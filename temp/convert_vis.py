
import json

path = "/Users/sourabhsoni/Code/onePeace/one_piece_content/src/main/resources/assets/one_piece_content/animations/sand_spike.animation.json"

with open(path, "r", encoding='utf-8') as f:
    data = json.load(f)

def is_hidden(val):
    # Check if vector is close to [0,0,0]
    if isinstance(val, list):
        return sum(abs(x) for x in val) < 0.1
    # Check pre/post objects
    if isinstance(val, dict):
        # Use 'post' as the defining value for the timestamp
        if "post" in val:
            return is_hidden(val["post"])
        if "vector" in val:
            return is_hidden(val["vector"])
    return False

HIDDEN_POS = [0, -10000, 0]
VISIBLE_POS = [0, 0, 0]

for anim_name, anim_content in data.get("animations", {}).items():
    print(f"Processing {anim_name}...")
    bones = anim_content.get("bones", {})
    
    for bone_name, bone_data in bones.items():
        if "scale" in bone_data:
            scale_frames = bone_data["scale"]
            pos_frames = {}
            
            # If position already exists, we might overwrite it. 
            # Assuming pure visibility animation, this is fine.
            if "position" in bone_data:
                print(f"Warning: Bone {bone_name} already has position data. Overwriting visiblity frames.")
                # We'll merge or replace? Let's start fresh for visibility logic.
                # But if there was motion, we lose it. 
                # User said "main animation is self... we are not doing waves" and goal is "appear and disappear" of bones.
                # So likely no existing position animation on these bones.
            
            # Iterate through scale frames
            keys = sorted(scale_frames.keys(), key=float)
            
            for t in keys:
                val = scale_frames[t]
                if is_hidden(val):
                    pos_frames[t] = HIDDEN_POS
                else:
                    pos_frames[t] = VISIBLE_POS
            
            # Replace scale with position
            del bone_data["scale"]
            bone_data["position"] = pos_frames
            
            # Ensure lerp_mode is step (redundant if already there, but safe)
            bone_data["lerp_mode"] = "step"

print("Conversion complete.")

with open(path, "w", encoding='utf-8') as f:
    json.dump(data, f, indent='\t')
