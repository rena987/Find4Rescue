

def findAddress(filepath, address):
  data = gpd.read_file(filepath)

  data_2 = data["PSTLADRESS"]
  wanted_index = -1;
  for index, value in data_2.items():
    if value != None:
      if address in value:
        wanted_index = index
        break;

  latitude = 0;
  longitude = 0;
  if wanted_index != -1:
    latitude = data.iloc[wanted_index]["LATITUDE"]
    longitude = data.iloc[wanted_index]["LONGITUDE"];

  return latitude + "," + longitude;
