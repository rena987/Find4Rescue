# Find4Rescue

## Wireframes

![20210713_191147](https://user-images.githubusercontent.com/47072485/125536837-3f3fbf2a-6383-4329-940a-0f2eb7308efe.jpg)


## User Stories

The following **required** functionality is completed:

* [ ]	Rescuer can **sign in** using OAuth login
* [ ]	Rescuer can **view people in need of help** in the search screen
  * [ ] Rescuer can see username, details on risk, urgency rating and associated pictures. (stored on Parse Database) 
  * [ ] Rescuer is displayed the [relative timestamp](https://gist.github.com/nesquena/f786232f5ef72f6e10a7) for each person "8m", "7h"
  * [ ] Rescuer can "like" a person in need on the search screen to indicate to other rescuers that they are dealing with it
  * [ ] Rescuer can search for a person in need based on username
  * [ ] Rescuer can filter out people in need based on urgency and most recent
  * [ ] User can **pull down to refresh search screen**
* [ ] Rescuer can **chat with person in need** using Parse Chat Application
  * [ ] Rescuer can **send and receive pictures** through this chat feature 
* [ ] Rescuer can **view arcGIS map** on map screen
  * [ ] Rescuer can view current location of person they are trying to rescue 
  * [ ] Rescuer can view locations of all the other rescuers 
  * [ ] Rescuer can view polygon lines of the county they are in (important in connecting with other rescuers!) 

The following **stretch** features are implemented:

* [ ]  "Person in need" can **sign in** using OAuth login
  * [ ] "Person in need" has access to more user-friendly **Google Maps SDK** instead of arcGIS map to show location of rescuer compared to their current location
  * [ ] "Person in need" does not have access to search screen but includes chat application and camera feature 
  * [ ] "Person in need" can **create new risk screen** which the Rescuer will see on their search screen
* [ ] User sees an **indeterminate progress indicator** when any background or network task is happening
* [ ] User can tap a risk in search screen to **open a detailed view**
* [ ] User can view more people at risk as they scroll with infinite pagination
* [ ] Replace all icon drawables and other static image assets with vector drawables
* [ ] Use the View Binding library to reduce view boilerplate.


## Schema

|    Property    | Type |  Description  |  
| -------------- | ---- | ------------- |
| objectId  | String  | unique id for "risk screen" (default field) |
| createdAt  | DateTime  | date when "risk screen" is created (default field) |
| updatedAt  | DateTime  | date when "risk screen" is last updated (default field) |
| author  | Pointer to User  | author of "risk screen" |
| image  | File  | image that author associated with risk |
| description  | String  | description of "risk" |
| dealtOrNo | Boolean | whether or not the "risk screen" is being dealt with already |


