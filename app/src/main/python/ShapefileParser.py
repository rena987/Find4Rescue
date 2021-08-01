import geopandas as gpd
import shapely.geometry
import fiona
import pyproj
from functools import partial
from shapely.geometry import box
from shapely.ops import transform

def findAddress(filepath, address):

  # Coordinates of exact point
  data = gpd.read_file(filepath)
  data_2 = data["PSTLADRESS"]
  wanted_index = -1
  for index, value in data_2.items():
    if value != None:
      if address in value:
        wanted_index = index
        break
  latitude = 0
  longitude = 0
  if wanted_index != -1:
    latitude = data.iloc[wanted_index]["LATITUDE"]
    longitude = data.iloc[wanted_index]["LONGITUDE"]
  coord = latitude, longitude

  # Coordinates of polygon
  shp = fiona.open(filepath, 'r')
  p_in = pyproj.Proj(shp.crs)
  bound_box = box(*data.geometry[wanted_index].bounds)
  shp.close()
  p_out = pyproj.Proj({'init': 'EPSG:4326'})  # aka WGS84
  project = partial(pyproj.transform, p_in, p_out)
  bound_box_wgs84 = transform(project, bound_box)
  x, y = bound_box_wgs84.exterior.coords.xy

  z = coord, x, y
  print(z)
  print(data_2[wanted_index])


  return z

findAddress("/Users/serenabehera/Downloads/WisconsinParcels/V700_Wisconsin_Parcels_OZAUKEE.shp", "132 E KANE ST")