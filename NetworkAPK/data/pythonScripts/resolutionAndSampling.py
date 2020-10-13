import bpy

for scene in bpy.data.scenes:
    scene.render.resolution_x = 512
    scene.render.resolution_y = 512

bpy.context.scene.cycles.samples = 128
