import shapely.geometry
import numpy as np
from functools import partial
from shapely.geometry import box
from shapely.ops import transform
import fiona
import geopandas as gpd
import pyproj

def findAddress(filepath, address):

  upper_address = address.upper()

  # Retrieve index of address in shapefile
  data = gpd.read_file(filepath)
  data_addresses = data["ADDRESS1"]
  wanted_index = -1
  for index, value in data_addresses.items():
    if value is not None:
      if upper_address in value:
        wanted_index = index
        break

  # Retrieve coordinates of polygon and convert it to correct spatial reference
  shp = fiona.open(filepath, 'r')
  p_in = pyproj.Proj(shp.crs)
  bound_box = box(*data.iloc[wanted_index].geometry.bounds)
  shp.close()
  p_out = pyproj.Proj({'init': 'EPSG:4326'})  # aka WGS84
  project = partial(pyproj.transform, p_in, p_out)
  bound_box_wgs84 = transform(project, bound_box)
  x, y = bound_box_wgs84.exterior.coords.xy

  z = x, y
  print(z)

  return z


findAddress("/Users/serenabehera/Downloads/OrangeCountyParcels/parview.shp", "110 GLENMORE RD")