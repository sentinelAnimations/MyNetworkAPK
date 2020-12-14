import bpy
bpy.context.scene.cycles.device = 'CPU'
bpy.ops.render.render(True)

for scene in bpy.data.scenes:
    scene.render.tile_x = 32
    scene.render.tile_y = 32