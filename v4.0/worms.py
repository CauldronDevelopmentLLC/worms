#!/usr/bin/env python3

import wx
from wx.glcanvas import *

from OpenGL.GLUT import *
from OpenGL.GLU import *
from OpenGL.GL import *

import random
import time
import math
import copy

maxX = 800#wx.DisplaySize()[0] - 1
maxY = 600#wx.DisplaySize()[1] - 1
maxZ = int((maxX + maxY) / 2)

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
  def __init__(self, size, step, shape, color = None):
    self.size = size
    self.step = step

    if shape == RANDOM: self.shape = random.randint(1, 5)
    else: self.shape = shape

    self.x = random.randint(0, maxX)
    self.y = random.randint(0, maxY)
    self.z = random.randint(0, maxZ)
    self.lastX = self.lastY = self.lastZ = None
    self.r = random.random()
    self.rot = [0, 1, 1, 1]
    self.rot_type = random.getrandbits(1)

    if color == None:
      self.color = Color()
      self.color.randomize()
    else: self.color = color
    self.colorStep = randExpInt(3)

    self.rExp = random.randint(0, 8)
    self.fullStep = random.getrandbits(1) == 0


  def draw(self, dc):
    if self.lastX is not None:
      glLoadIdentity()
      gluLookAt(0, 0, 10, 0, 0, 0, 0, 1, 0)
      glTranslatef(0.5 - float(self.x) / float(maxX),
             0.5 - float(self.y) / float(maxY),
             float(self.z) / float(maxZ))

    self.lastX = self.x
    self.lastY = self.y
    self.lastZ = self.z


    self.color.alter(self.colorStep)
    c = self.color
    #dc.SetPen(wx.Pen(c, 1))
    #dc.SetBrush(wx.Brush(c))
    #glColor3f(1, 1, 1)

    if self.shape == POINT: r = 1
    else: r = randExpInt(self.rExp)

    if self.shape == LINE: size = self.size
    else: size = int(self.size / r)

    for i in range(size):
      if self.shape == POINT:
        self.r *= 1.0 + (0.5 - random.random()) * 0.1
        if self.r < 1: self.r = 1
        if 100 < self.r: self.r = 100
        #self.r += (0.5 - random.random()) * 0.1

        glPushMatrix()

        if self.rot_type:
          self.rot[0] += (0.5 - random.random())
          self.rot[1] += (0.5 - random.random()) * 0.1
          self.rot[2] += (0.5 - random.random()) * 0.1
          self.rot[3] += (0.5 - random.random()) * 0.1
          glRotate(*self.rot)

        else:
          glRotate(360 * (0.5 - random.random()),
               (0.5 - random.random()) * 2,
               (0.5 - random.random()) * 2,
               (0.5 - random.random()) * 2)

        color = [c.red(), c.green(), c.blue(), 0.25]
        glMaterialfv(GL_FRONT, GL_DIFFUSE, color)
        glMaterialfv(GL_FRONT, GL_SPECULAR, color)
        glMaterialfv(GL_FRONT, GL_AMBIENT,
               [c.red() / 2, c.green() / 2, c.blue() / 2, 1])
        glutSolidCube(self.r * 0.002)
        glPopMatrix()

        size = self.size

      elif self.shape == SQUARE:
        pass
        #dc.DrawRectangle(self.x, self.y, r, r)

      elif self.shape == LINE:
        x2 = alter(self.x, 0, maxX, random.randint(0, r), False)
        y2 = alter(self.y, 0, maxY, random.randint(0, r), False)
        #dc.DrawLine(self.x, self.y, x2, y2)

      elif self.shape == TRIANGLE:
        x2 = alter(self.x, 0, maxX, random.randint(0, r), False)
        y2 = alter(self.y, 0, maxY, random.randint(0, r), False)
        x3 = alter(self.x, 0, maxX, random.randint(0, r), False)
        y3 = alter(self.y, 0, maxY, random.randint(0, r), False)
        #dc.DrawPolygon([wx.Point(self.x, self.y), wx.Point(x2, y2),
        #  wx.Point(x3, y3)])

      elif self.shape == CIRCLE:
        dc.DrawCircle(self.x, self.y, r)

      elif self.shape == RCIRCLE:
        x2 = alter(self.x, 0, maxX, random.randint(0, r), False)
        y2 = alter(self.y, 0, maxY, random.randint(0, r), False)
        #dc.DrawCircle(x2, y2, r)

      elif self.shape == RSQUARE:
        x2 = alter(self.x, 0, maxX, random.randint(0, r), False)
        y2 = alter(self.y, 0, maxY, random.randint(0, r), False)
        #dc.DrawRectangle(x2, y2, r, r)


      step = random.randint(1, self.step)
      if self.shape != POINT and self.shape != LINE and self.fullStep:
        step = step + r * (step - 1)

      self.x = alter(self.x, 0, maxX, step / 2.0)
      self.y = alter(self.y, 0, maxY, step / 2.0)
      self.z = alter(self.z, 0, maxZ, step / 2.0)

    return self.size * r


class Main(GLCanvas):
  def __init__(self, frame):
    GLCanvas.__init__(self, frame, -1)

    self.context = GLContext(self)

    self.frame = frame
    self.time = 0
    self.duration = 60
    self.init = False

    self.Bind(wx.EVT_LEFT_DOWN, self.quit)
    self.Bind(wx.EVT_RIGHT_DOWN, self.reset)
    self.Bind(wx.EVT_PAINT, self.paint)

    # Hide cursor
    self.SetCursor(wx.StockCursor(wx.CURSOR_BLANK))

    frame.ShowFullScreen(True, style = wx.FULLSCREEN_ALL)
    #frame.SetBackgroundColour("black")
    #self.SetTransparent(100)

    #self.timer = wx.Timer(self, 1)
    #self.timer.Start(2)
    #wx.EVT_TIMER(self, 1, self.paint)


  def gl_init(self, size):
    self.SetCurrent(self.context)

    maxX = wx.DisplaySize()[0] - 1
    maxY = wx.DisplaySize()[1] - 1

    light_diffuse = [1.0, 1.0, 1.0, 1.0]
    light_position = [1.0, 1.0, 1.0, 0.0]

    glLightfv(GL_LIGHT0, GL_DIFFUSE, light_diffuse)
    glLightfv(GL_LIGHT0, GL_POSITION, light_position)

    glEnable(GL_LIGHTING)
    glEnable(GL_LIGHT0)
    glEnable(GL_DEPTH_TEST)
    glClearColor(0.0, 0.0, 0.0, 1.0)
    glClearDepth(1.0)

    glEnable(GL_LINE_SMOOTH);
    glEnable(GL_POINT_SMOOTH);
    glEnable(GL_POLYGON_SMOOTH);
    glShadeModel(GL_SMOOTH);
    glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
    glEnable(GL_BLEND);

    glMatrixMode(GL_PROJECTION)
    glLoadIdentity()
    gluPerspective(7.0, 1.0, 1.0, 1000.0)
    #glOrtho(0, size.x, size.y, 0, 0, 100)

    glMatrixMode(GL_MODELVIEW)
    glLoadIdentity()
    gluLookAt(0, 0, 10, 0, 0, 0, 0, 1, 0)

    '''
    glMatrixMode(GL_PROJECTION)
    glLoadIdentity()
    glOrtho(0, size.x, size.y, 0, 0, 100)

    glMatrixMode(GL_MODELVIEW)
    glLoadIdentity()
    '''

    #glClearColor(0, 0, 0, 0)

    #glDisable(GL_DEPTH_TEST)

    #self.clear_screen()

    glutInit(sys.argv);


  def clear_screen(self):
    glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT)
    #dc = wx.ClientDC(self)
    #dc.SetBrush(wx.Brush("black"))
    #dc.DrawRectangle(0, 0, maxX + 1, maxY + 1)


  def reset(self, event):
    if random.randint(0, 1) == 0: self.clear_screen()

    count = randExpInt(randExpInt(3))
    size = randExpInt(8)

    colorFormat = random.randint(0, 1)
    colors = random.randint(0, 2)
    color = Color(format = colorFormat)
    color.randomize()
    colorStep = randExpInt(7)

    wormStep = randExpInt(3)
    shape = 0
    #shape = random.randint(0, 7)

    s = f'Count:{count} Size {size} Color Format '
    if colorFormat == RGB: s += 'RGB'
    else: s += 'HSV'
    s += ' Colors:'
    if colors == 0: s += 'Uniform'
    elif colors == 1: s += 'Altered'
    else: s += 'Random'
    s += f' Color Step:{colorStep} Worm Step:{wormStep}'
    s += 'Shape:'
    if shape == RANDOM: s += 'Random'
    elif shape == POINT: s += 'Point'
    elif shape == SQUARE: s += 'Square'
    elif shape == LINE: s += 'Line'
    elif shape == TRIANGLE: s += 'Triangle'
    elif shape == CIRCLE: s += 'Circle'
    elif shape == RCIRCLE: s += 'Random Circle'
    elif shape == RSQUARE: s += 'Random Square'
    print(s)

    self.worms = []
    self.index = 0
    for x in range(count):
      self.worms.append(Worm(size, wormStep, shape, color))

      if colors != 0:
        color = copy.deepcopy(color)
        if colors == 1: color.alter(colorStep)
        else: color.randomize()

    self.time = time.time()


  def paint(self, event):
    self.SetCurrent(self.context)
    dc = wx.PaintDC(self)

    if not self.init:
      self.gl_init(self.GetSize())
      self.clear_screen()
      self.init = True


    self.SwapBuffers()

    self.run(None)

    glFlush()
    self.SwapBuffers()
    self.Refresh()


  def run(self, event):
    if self.time + self.duration < time.time(): self.reset(None)

    for worm in self.worms: worm.draw(None)


  def quit(self, event):
    #self.timer.Stop()
    self.frame.Destroy()


class App(wx.App):
  def __init__(self):
    wx.App.__init__(self, redirect=False)

  def OnInit(self):
    self.frame = wx.Frame(None, -1, 'worms', pos = wx.DefaultPosition,
      size = wx.DisplaySize())
    self.frame.Show(True)
    self.window = Main(self.frame)

    return True


App().MainLoop()
