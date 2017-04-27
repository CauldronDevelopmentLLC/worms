#!/usr/bin/python

import wx
import BaseHTTPServer
import urlparse
import random
import time
import math
import copy
import cv
import array
import Image

PORT = 8000

RGB = 0
HSV = 1

RED = 0
GREEN = 1
BLUE = 2

HUE = 0
SATURATION = 1
VALUE = 2

POINT = 0
SQUARE = 1
LINE = 2
CIRCLE = 3
TRIANGLE = 4
RSQUARE = 5
RCIRCLE = 6
RANDOM = 7

def alter(x, min, max, step = 1, wrap = True):
    max -= min

    if random.getrandbits(1):
        x += step
        if wrap:
            while x > max: x -= max
        elif x > max: x = max
    else:
        x -= step
        if wrap:
            while x < 0: x += max
        elif x < 0: x = 0

    return x + min


def randExpInt(pow):
    return 2 ** random.randint(0, pow)


class Color:
    def __init__(self, data = [0.0, 0.0, 0.0], format = RGB):
        self.format = format
        if format == RGB:
            self.rgb = data
            self.hsv = self.RGBToHSV(data)
        else:
            self.hsv = data
            self.rgb = self.HSVToRGB(data)

    def WX(self):
        return wx.Colour(self.redByte(), self.greenByte(), self.blueByte())

    def red(self):
        return self.rgb[RED]

    def green(self):
        return self.rgb[GREEN]

    def blue(self):
        return self.rgb[BLUE]

    def redByte(self):
        return int(self.red() * 255)

    def greenByte(self):
        return int(self.green() * 255)

    def blueByte(self):
        return int(self.blue() * 255)

    def randomize(self):
        self.rgb = [random.random(), random.random(), random.random()]
        self.hsv = self.RGBToHSV(self.rgb)

    def alter(self, step = 1, format = None):
        if format == None: format = self.format

        x = random.randint(0, 2)

        if format == HSV:
            if x == HUE:
                self.hsv[HUE] = alter(self.hsv[HUE], 0, 360, step)
            else:
                self.hsv[x] = alter(self.hsv[x], 0, 1, step / 255.0, False)

            self.rgb = self.HSVToRGB(self.hsv)
        else:
            self.rgb[x] = alter(self.rgb[x], 0, 1, step / 255.0)
            self.hsv = self.RGBToHSV(self.rgb)


    def RGBToHSV(self, rgb):
        MAX = max(rgb)
        MIN = min(rgb)

        if MAX == MIN: h = 0
        elif MAX == rgb[RED]:
            h = (60 * (rgb[GREEN] - rgb[BLUE]) / (MAX - MIN) + 360) % 360
        elif MAX == rgb[GREEN]:
            h = 60 * (rgb[BLUE] - rgb[RED]) / (MAX - MIN) + 120
        else:
            h = 60 * (rgb[RED] - rgb[GREEN]) / (MAX - MIN) + 240

        if MAX == 0: s = 0
        else: s = 1 - MIN / MAX

        return [h, s, MAX]


    def HSVToRGB(self, hsv):
        f = hsv[HUE] / 60.0 - math.floor(hsv[HUE] / 60.0)

        p = hsv[VALUE] * (1 - hsv[SATURATION])
        q = hsv[VALUE] * (1 - f * hsv[SATURATION])
        t = hsv[VALUE] * (1 - (1 - f) * hsv[SATURATION])

        i = int(math.floor(hsv[HUE] / 60.0)) % 6

        if i == 0: return [hsv[VALUE], t, p]
        if i == 1: return [q, hsv[VALUE], p]
        if i == 2: return [p, hsv[VALUE], t]
        if i == 3: return [p, q, hsv[VALUE]]
        if i == 4: return [t, p, hsv[VALUE]]
        if i == 5: return [hsv[VALUE], p, q]


class Worm:
    def __init__(self, size, step, shape, maxX, maxY, color = None):
        self.size = size
        self.step = step

        if shape == RANDOM: self.shape = random.randint(1, 5)
        else: self.shape = shape

        self.x = random.randint(0, maxX)
        self.y = random.randint(0, maxY)

        if color == None:
            self.color = Color()
            self.color.randomize()
        else: self.color = color
        self.colorStep = randExpInt(3)

        self.rExp = random.randint(0, 8)
        self.fullStep = random.getrandbits(1) == 0


    def draw(self, dc):
        self.color.alter(self.colorStep)
        c = self.color.WX()
        dc.SetPen(wx.Pen(c, 1))
        dc.SetBrush(wx.Brush(c))
        maxX, maxY = dc.GetSize()

        if self.shape == POINT: r = 1
        else: r = randExpInt(self.rExp)

        if self.shape == LINE: size = self.size
        else: size = self.size / r

        for i in range(size):
            if self.shape == POINT:
                dc.DrawPoint(self.x, self.y)
                size = self.size

            elif self.shape == SQUARE:
                dc.DrawRectangle(self.x, self.y, r, r)

            elif self.shape == LINE:
                x2 = alter(self.x, 0, maxX, random.randint(0, r), False)
                y2 = alter(self.y, 0, maxY, random.randint(0, r), False)
                dc.DrawLine(self.x, self.y, x2, y2)

            elif self.shape == TRIANGLE:
                x2 = alter(self.x, 0, maxX, random.randint(0, r), False)
                y2 = alter(self.y, 0, maxY, random.randint(0, r), False)
                x3 = alter(self.x, 0, maxX, random.randint(0, r), False)
                y3 = alter(self.y, 0, maxY, random.randint(0, r), False)
                dc.DrawPolygon([wx.Point(self.x, self.y), wx.Point(x2, y2),
                                wx.Point(x3, y3)])

            elif self.shape == CIRCLE:
                dc.DrawCircle(self.x, self.y, r)

            elif self.shape == RCIRCLE:
                x2 = alter(self.x, 0, maxX, random.randint(0, r), False)
                y2 = alter(self.y, 0, maxY, random.randint(0, r), False)
                dc.DrawCircle(x2, y2, r)

            elif self.shape == RSQUARE:
                x2 = alter(self.x, 0, maxX, random.randint(0, r), False)
                y2 = alter(self.y, 0, maxY, random.randint(0, r), False)
                dc.DrawRectangle(x2, y2, r, r)


            step = random.randint(1, self.step)
            if self.shape != POINT and self.shape != LINE and self.fullStep:
                step = step + r * (step - 1)

            if random.getrandbits(1): self.x = alter(self.x, 0, maxX, step)
            else: self.y = alter(self.y, 0, maxY, step)

        return self.size * r


class Main(wx.Frame, BaseHTTPServer.HTTPServer):
    def __init__(self):
        wx.Frame.__init__(self, None, wx.ID_ANY, 'Worms', pos = (0, 0),
                          size = wx.DisplaySize())

        self.frame = 0
        self.clear_count = 0
        self.reset_count = 0

        # HTTP Server
        class HTTPHandler(BaseHTTPServer.BaseHTTPRequestHandler):
            def do_HEAD(request):
                request.send_response(200)
                request.send_header('Content-type', 'text/html')
                request.end_headers()

            def do_GET(request):
                self.do_GET(request)

        BaseHTTPServer.HTTPServer.__init__(self, ('', PORT), HTTPHandler)
        self.timeout = 0

        self.on_size(None)

        self.Bind(wx.EVT_LEFT_DOWN, self.on_quit)
        self.Bind(wx.EVT_RIGHT_DOWN, self.on_reset)
        self.Bind(wx.EVT_SIZE, self.on_size)

        # Hide cursor
        self.SetCursor(wx.StockCursor(wx.CURSOR_BLANK))

        #self.ShowFullScreen(True, style = wx.FULLSCREEN_ALL)
        self.SetBackgroundColour("black")
        #self.SetTransparent(100)
        self.clear()
        self.reset()
        self.Show(True)

        self.timer = wx.Timer(self, 1)
        self.timer.Start(1000 / 60.0) # 60Hz
        wx.EVT_TIMER(self, 1, self.run)

        self.reset_timer = wx.Timer(self, 2)
        self.reset_timer.Start(60000)
        wx.EVT_TIMER(self, 2, self.reset)


    def do_GET(self, request):
        path = urlparse.urlparse(request.path)
        request.send_response(200)

        if path.path in ['/reset', '/clear', '/reset_and_clear']:
            if path.path == '/reset': self.reset()
            if path.path == '/clear': self.clear()
            if path.path == '/reset_and_clear':
                self.clear()
                self.reset()

            request.send_header('Content-type', 'text/html')
            request.end_headers()
            request.wfile.write('<html><head>'
                                '<meta http-equiv="REFRESH" content="0;url=/"/>'
                                '</head><body bgcolor="black"</body></html>')

        elif path.path.startswith('/worms-'):
            request.send_header('Content-type', 'image/png')
            request.end_headers()
            self.save_to_file(request.wfile)

        else:
            request.send_header('Content-type', 'text/html')
            request.end_headers()
            now = time.strftime('%Y%m%d-%H%M%S')
            request.wfile.write('<html><body bgcolor="black" text="white">'
                                '<center>'
                                '<img src="worms-' + now + '.png"/>'
                                '<h2>' + self.description + '</h2>'
                                '<h3>'
                                '<a href="reset">Reset</a> '
                                '<a href="clear">Clear</a> '
                                '<a href="reset_and_clear">Reset & Clear</a> '
                                '<a href="/">Reload</a>'
                                '</h3>'
                                '</center></body></html>')


    def save_to_file(self, file):
        image = self.bitmap.ConvertToImage()
        image.SetOptionInt('quality', 100)
        out = wx.OutputStream(file)
        image.SaveStream(out, wx.BITMAP_TYPE_PNG)


    def on_size(self, event):
        self.width, self.height = self.GetClientSize()
        self.bitmap = wx.EmptyBitmap(self.width, self.height)
        self.mdc = wx.MemoryDC(self.bitmap)

        w, h = self.width, self.height
        self.image = cv.CreateImage((w, h), cv.IPL_DEPTH_8U, 3)
        self.buffer = array.array('B', ' ' * (w * h * 3))

        self.clear()


    def clear(self):
        self.mdc.SetPen(wx.Pen('black', 1))
        self.mdc.SetBrush(wx.Brush('black'))
        self.mdc.DrawRectangle(0, 0, *self.GetClientSize())


    def on_reset(self, event = None):
        self.reset_timer.Start() # Restart timer
        self.reset()


    def reset(self, event = None):
        if random.randint(0, 1) == 0:
            self.clear()
            self.reset_count = 0
        else: self.reset_count += 1

        count = randExpInt(randExpInt(4))
        size = randExpInt(12)

        colorFormat = random.randint(0, 1)
        colors = random.randint(0, 2)
        color = Color(format = colorFormat)
        color.randomize()
        colorStep = randExpInt(7)

        wormStep = randExpInt(3)
        shape = random.randint(0, 7)

        s = ''
        s += "Count:%d Size:%d Color Format:" % (count, size)
        if colorFormat == RGB: s += "RGB"
        else: s += "HSV"
        s += " Colors:"
        if colors == 0: s += "Uniform"
        elif colors == 1: s += "Altered"
        else: s += "Random"
        s += " Color Step:%d Worm Step:%d" % (colorStep, wormStep)
        s += " Shape:"
        if shape == RANDOM: s += "Random"
        elif shape == POINT: s += "Point"
        elif shape == SQUARE: s += "Square"
        elif shape == LINE: s += "Line"
        elif shape == TRIANGLE: s += "Triangle"
        elif shape == CIRCLE: s += "Circle"
        elif shape == RCIRCLE: s += "Random Circle"
        elif shape == RSQUARE: s += "Random Square"

        self.description = s
        print s

        self.worms = []
        self.index = 0
        for x in range(count):
            worm = Worm(size, wormStep, shape, self.width, self.height, color)
            self.worms.append(worm)

            if colors != 0:
                color = copy.deepcopy(color)
                if colors == 1: color.alter(colorStep)
                else: color.randomize()


    def run(self, event):
        size = 0
        while size < 1000:
            size += self.worms[self.index].draw(self.mdc)
            self.index += 1
            if self.index >= len(self.worms): self.index = 0

        dc = wx.PaintDC(self)
        w, h = self.mdc.GetSize()
        dc.Blit(0, 0, w, h, self.mdc, 0, 0)

        self.handle_request()


    def on_quit(self, event):
        self.timer.Stop()
        self.Destroy()

if __name__ == '__main__':
    wx.Image.AddHandler(wx.PNGHandler())
    app = wx.PySimpleApp()
    main = Main()
    app.MainLoop()
