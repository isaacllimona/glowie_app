-- @name hue sweep
-- @description smoothly sweep through all hues

h = 0
while 1 do
	h = h + 1
	if h == 180 then h = 0 end
	led.setColorHSV(h, 255, 255)
	sys.sleep(30)
end
