package main

import "fmt"

// ===== Observer Interface =====
type Observer interface {
	update(string)
	getID() string
}

// ===== Subject Interface =====
type Subject interface {
	register(observer Observer)
	deregister(observer Observer)
	notifyAll()
}

// ===== Concrete Subject =====
type Item struct {
	observerList []Observer
	name         string
	inStock      bool
}

func newItem(name string) *Item {
	return &Item{
		name: name,
	}
}

func (i *Item) updateAvailability() {
	fmt.Printf("Item %s is now in stock\n", i.name)
	i.inStock = true
	i.notifyAll() 
}

func (i *Item) register(o Observer) {
	i.observerList = append(i.observerList, o)
}

func (i *Item) deregister(o Observer) {
	i.observerList = removeFromSlice(i.observerList, o)
}

func (i *Item) notifyAll() {
	for _, observer := range i.observerList {
		observer.update(i.name)
	}
}
func removeFromSlice(observerList []Observer, observerToRemove Observer) []Observer {
	length := len(observerList)
	for i, observer := range observerList {
		if observer.getID() == observerToRemove.getID() {
			observerList[i] = observerList[length-1]
			return observerList[:length-1]
		}
	}
	return observerList
}

// ===== Concrete Observer =====
type Customer struct {
	id string
}

func (c *Customer) update(itemName string) {
	fmt.Printf("Sending email to customer %s for item %s\n", c.id, itemName)
}

func (c *Customer) getID() string {
	return c.id
}

// ===== Main =====
func main() {
	shirtItem := newItem("Nike Shirt")

	observerFirst := &Customer{id: "PetroPetrenko@gmail.com"}
	observerSecond := &Customer{id: "JonDow@gmail.com"}

	shirtItem.register(observerFirst)
	shirtItem.register(observerSecond)

	shirtItem.updateAvailability()
}