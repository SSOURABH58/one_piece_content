
import json
import io

file_path = "/Users/sourabhsoni/Code/onePeace/one_piece_content/src/main/resources/assets/one_piece_content/animations/sand_spike.animation.json"

try:
    with open(file_path, "r", encoding='utf-8') as f:
        data = json.load(f)
except Exception as e:
    print(f"Error loading JSON: {e}")
    exit(1)

# Navigate to the specific animation
# Assuming structure: { "animations": { "animation.sand_spike": { "bones": { ... } } } }
anims = data.get("animations", {})
target_anim_key = "animation.sand_spike"

if target_anim_key in anims:
    anim_data = anims[target_anim_key]
    bones = anim_data.get("bones", {})
    
    for bone_name, bone_data in bones.items():
        # User requested adding 'lerp_mode': 'step' to "every bone"
        # Usually this isn't a top-level bone property in standard bedrock/geckolib schemas,
        # but GeckoLib sometimes accepts it or it needs to be on keyframes.
        # User prompt: "add lerp_mode: "step" to every bone in the animaction"
        # We will add it to the bone object itself.
        
        # NOTE: Standard Bedrock JSON doesn't support 'lerp_mode' on bones usually,
        # but modern GeckoLib might use it or user means "set interpolation to step for all channels".
        # However, to strictly follow "add ... to every bone", we add the key.
        # BUT, standard practice for "catmull-rom" vs "linear" is setting it per keyframe or using specific format.
        # The user specifically asked for "lerp_mode: step".
        
        bone_data["lerp_mode"] = "step"

    print(f"Added 'lerp_mode': 'step' to {len(bones)} bones in {target_anim_key}.")
else:
    print(f"Animation '{target_anim_key}' not found.")

# Write back
try:
    with open(file_path, "w", encoding='utf-8') as f:
        json.dump(data, f, indent='\t') # Try to maintain tab indentation
    print(f"Successfully updated {file_path}")
except Exception as e:
    print(f"Error writing file: {e}")
