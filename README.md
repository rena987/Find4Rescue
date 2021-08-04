# Find4Rescue

## Wireframes

![Wireframes2](https://user-images.githubusercontent.com/47072485/125831227-3d936594-abdf-4649-b568-93a861e90172.jpg)


## User Stories

The following **required** functionality is completed:

* [x]	Rescuer can **sign in** to his or her account
* [x]	Rescuer can **sign out** to his or her account
* [x]	Rescuer can **log out** from his or her account
* [x]	Rescuer can **view people in need of help** in the Search View
  * [x] Rescuer can see details of risk (stored on Parse Database) within the main Search View such as: 
    * [x] Primary Rescuer who reported the risk
    * [x] Address of the Risk
    * [x] Type of Risk
    * [x] Description of the Risk
    * [x] The [relative timestamp](https://gist.github.com/nesquena/f786232f5ef72f6e10a7) when each risk was reported (eg. "8m", "7h")
   * [x] Rescuer can create a new "risk" on search screen through given: 
     * [x] Coordinates
     * [x] Address
     * [x] Type
     * [x] Description
     * [x] Image
       * [x] Rescuer can attach an image from their drive 
       * [x] Rescuer can take picture from their device
   * [x] Rescuer can filter out risks based on most recent and rescuers needed
   * [ ] Rescuer can double tap on a risk to address it (gesture) 
* [x] Rescuer can **view a detailed view** of each risk in the Search View 
  * [x] Rescuer can access the detailed view by:
    * [x] Tap on individual risk from the search view 
    * [x] Open search detail view using shared element transition (Animation) 
  * [x] Rescuer can view all the details they saw on the search screen plus the number of existing rescuers addressing the risk
  * [x] Rescuer can "like" or press the heart button to indicate to other rescuers that they are also addressing the risk by incrementing the number of existing rescuers displayed 
  * [x] Rescuer can comment and view other rescuers comments on the risk by pressing the "message" button
  * [x] Rescuer can **view arcGIS map** by pressing the "map" button 
    * [x] Rescuer can view parcel polygon lines of the county they are in (Becker County)
    * [x] Rescuer can plot highlighted parcel polygon they have selected to help based on the risk they click on the search view
    * [x] Rescuer can view details of the highlighted parcel polygon:
       * [x] Parcel Identification Number
       * [x] Subdivision
       * [x] Complete Address
* [x] Rescuer can press on camera button in the bottom navigation view to take picture in order to:
  * [x] Make new risk

The following **stretch** features are implemented:

* [ ] Rescuer sees an **indeterminate progress indicator** when any background or network task is happening
* [ ] Rescuer can find the distance between themselves and the risk they are dealing with on arcGIS map 
* [ ] Rescuer can search for a "risk" based on type
* [ ] Rescuer can view more people at risk as they scroll with infinite pagination
* [ ] "Person in need" has there own login where they can chat with rescuers 
* [ ] Replace all icon drawables and other static image assets with vector drawables
* [ ] Use the View Binding library to reduce view boilerplate.
* [ ] Rescuer can **pull down to refresh search screen**
* [ ] Use 2-3 more counties as data for the arcGIS map 


# Schema

## Risk
|    Property    | Type |  Description  |  
| -------------- | ---- | ------------- |
| objectId  | String  | unique id for "risk screen" (default field) |
| createdAt  | DateTime  | date when "risk screen" is created (default field) |
| updatedAt  | DateTime  | date when "risk screen" is last updated (default field) |
| Address  | String  | address of where the risk is ocurring |
| Type | String | type of risk |
| Rescuer  | Pointer to User  | author of "risk screen" |
| Image  | File  | image that rescuer attached to risk |
| Description  | String  | description of "risk" |
| DealtOrNot | Boolean | whether or not the "risk screen" is being dealt with already |
| NumOfRescuers | Number | number of rescuers dealing with the risk | 

## Comments
|    Property    | Type |  Description  |  
| -------------- | ---- | ------------- |
| objectId  | String  | unique id for "risk screen" (default field) |
| createdAt  | DateTime  | date when "risk screen" is created (default field) |
| updatedAt  | DateTime  | date when "risk screen" is last updated (default field) |
| CommentedRisk  | Pointer to Risk  | Risk on which is being commented on |
| Usernames | Array | Array of strings of the usernames within the comments |
| Messages  | Array  | Array of strings of the messages corresponding to the usernames |

