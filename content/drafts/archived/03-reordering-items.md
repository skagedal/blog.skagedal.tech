In my habit tracker, I would like to be able to reorder the items in the list. Typically, some my habits will be performed in some sequence during the day, and it would be nice to have the ones that are done first at the top.    

The question becomes on how to store this order. There are many ways this could be modeled.

1. I could make each habit have a `position` value. This would be a simple integer – if I have five habits, they will have positions from 1 to 5. This means that if I want to change the order of one item, I would have to update all the other habits when I change the order of one.
2. I could make that order value something non-discrete. For example, I could make it a float. This would allow me to insert a new item between two existing items by just picking a value right inbetween the middle of the order values. If I'm worried about the granularity of flo  
3. I could make it a linked list – each habit would link to its previous and next item.
4. 

The accepted answer to [this question](https://softwareengineering.stackexchange.com/questions/195308/storing-a-re-orderable-list-in-a-database) convinced me that I should store the order as a simple property. 

