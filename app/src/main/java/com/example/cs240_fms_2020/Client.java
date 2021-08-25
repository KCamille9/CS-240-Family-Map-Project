package com.example.cs240_fms_2020;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import Model.Event;
import Model.Person;


public class Client
{
    private Map<String, Person> peopleMap;
    private Map<String, Event> eventsMap;

    private Map<String, Event> createdEventsInMap;
    private Map<String, List<Event>> peopleEventsMap;

    private Settings settings;

    private List<String> eventTypes;
    private Map <String, EventColor> eventColorsMap;
    private Person person;

    private Set<String> paternalAncestors;
    private Set<String> maternalAncestors;
    private Map<String, Person> childrenMap;

    private String serverHost;
    private String ip;
    private String authToken;

    private Person chosenPerson;
    private Event chosenEvent;

    private static Client instance;

    public static Client init()
    {
        if (instance == null){
            instance = new Client();
        }
        return instance;
    }

    public boolean isPersonTypeInSettings(Person currPerson)
    {
        boolean noMaleEvents = !settings.isMaleEvents();
        boolean malePerson = currPerson.getGender().toLowerCase().equals("m")
                || currPerson.getGender().toLowerCase().equals("male");

        boolean noFemaleEvents = !settings.isFemaleEvents();
        boolean femalePerson = currPerson.getGender().toLowerCase().equals("f")
                || currPerson.getGender().toLowerCase().equals("female");

        boolean noFatherAncestry = !settings.isFatherSideEvents();
        boolean paternalAncestor = paternalAncestors.contains(currPerson.getPersonID());

        boolean motherAncestry = settings.isMotherSideEvents();
        boolean notMaternalAncestor = !maternalAncestors.contains(currPerson.getPersonID());

        if (noMaleEvents && malePerson)
        {
            return false;
        }
        else if (noFemaleEvents && femalePerson)
        {
            return false;
        }
        else if (noFatherAncestry && paternalAncestor)
        {
            return false;
        }
        else return motherAncestry || notMaternalAncestor;
    }

    public List<Event> sortEventsByYear(List<Event> events)
    {
        List<Event> sortedEventsList = new ArrayList<>();
        List<Event> currList = new ArrayList<>(events);

        while(currList.size() > 0)
        {
            Event currEvent = currList.get(0);
            int index = 0;
            for (int i = 0; i < currList.size(); i++)
            {
                if (currList.get(i).getYear() < currEvent.getYear())
                {
                    currEvent = currList.get(i);
                    index = i;
                }
            }

            sortedEventsList.add(currEvent);
            currList.remove(index);
        }
        return sortedEventsList;
    }

    public List<Person> findRelatives(String personID)
    {
        Person currPerson = getPeopleMap().get(personID);
        List<Person> personList = new ArrayList<>();

        Person personSpouse = getPeopleMap().get(currPerson.getSpouseID());
        Person personMother = getPeopleMap().get(currPerson.getMotherID());
        Person personFather = getPeopleMap().get(currPerson.getFatherID());
        Person personChildren = getChildrenMap().get(currPerson.getPersonID());

        if(personSpouse != null)
        {
            personList.add(personSpouse);
        }
        if(personMother != null)
        {
            personList.add(personMother);
        }
        if(personFather != null)
        {
            personList.add(personFather);
        }
        if(personChildren != null)
        {
            personList.add(personChildren);
        }

        return personList;
    }

    public Map<String, Event> getCreatedEventsInMap()
    {
        createdEventsInMap = new HashMap<>();

        for (Event currEvent: eventsMap.values())
        {
            Person eventPerson = getPeopleMap().get(currEvent.getPersonID());

            if (!isPersonTypeInSettings(eventPerson))
            {
            }
            else if (!settings.containsEventType(currEvent.getEventType()))
            {
            }
            else
            {
                createdEventsInMap.put(currEvent.getEventID(), currEvent);
            }
        }
        return createdEventsInMap;
    }

    public void setUpData()
    {
        setUpEventTypes();
        setUpPaternalTree();
        setUpMaternalTree();
        setUpPeopleEvents();
        setUpChildren();
        if (settings == null){
            settings = new Settings();
        }
    }

    private void setUpEventTypes()
    {
        ArrayList<Event> eventsArray = new ArrayList<>();
        eventColorsMap = new HashMap<>();
        eventTypes = new ArrayList<>();

        for (Event currEvent : eventsMap.values())
        {
            eventsArray.add(currEvent);
        }

        for (int i = 0; i < eventsArray.size(); i++)
        {
            String currEventType = eventsArray.get(i).getEventType().toLowerCase();
            if (!eventColorsMap.containsKey(currEventType))
            {
                EventColor colorForCurrEvent = new EventColor(currEventType);
                eventColorsMap.put(currEventType, colorForCurrEvent);
                eventTypes.add(currEventType);
            }
        }
        instance.setEventTypes(eventTypes);
    }

    private void setUpPaternalTree()
    {
        paternalAncestors = new HashSet<>();
        String personFather = person.getFatherID();
        ancestorHelper(personFather, paternalAncestors);
    }

    private void setUpMaternalTree()
    {
        maternalAncestors = new HashSet<>();
        String personMother = person.getMotherID();
        ancestorHelper(personMother, maternalAncestors);
    }

    //method that would traverse both side of the family ancestry
    private void ancestorHelper(String personID, Set<String> peopleSet)
    {
        if (personID == null)
        {
            return;
        }

        peopleSet.add(personID);
        Person currPerson = peopleMap.get(personID);

        //traverse through father side ancestry
        if (currPerson.getFatherID() != null)
        {
            String personFather = currPerson.getFatherID();
            ancestorHelper(personFather, peopleSet);
        }

        //traverse through mother side ancestry
        if (currPerson.getMotherID() != null)
        {
            String personMother = currPerson.getMotherID();
            ancestorHelper(personMother, peopleSet);
        }
    }

    //store every person event in peopleMap in peopleEventsMap
    private void setUpPeopleEvents()
    {
        peopleEventsMap = new HashMap<>();

        for (Person person: peopleMap.values())
        {
            ArrayList<Event> eventList = new ArrayList<>();

            for (Event event: eventsMap.values())
            {
                if (person.getPersonID().equals(event.getPersonID())){
                    eventList.add(event);
                }
            }
            peopleEventsMap.put(person.getPersonID(),eventList);
        }
    }

    //store every children found in childrenMap
    private void setUpChildren()
    {
        childrenMap = new HashMap<>();

        for (Person person: peopleMap.values())
        {
            //add children to children map if father found
            if (person.getFatherID() != null)
            {
                childrenMap.put(person.getFatherID(), person);
            }
            //add children to children map if mother found
            if (person.getMotherID() != null)
            {
                childrenMap.put(person.getMotherID(), person);
            }
        }
    }


    public Map<String, Person> getPeopleMap()
    {
        return peopleMap;
    }

    public void setPeopleMap(Map<String, Person> peopleMap)
    {
        this.peopleMap = peopleMap;
    }

    public Map<String, Event> getEventsMap()
    {
        return eventsMap;
    }

    public void setEventsMap(Map<String, Event> eventsMap)
    {
        this.eventsMap = eventsMap;
    }

    public void setPeopleEventsMap(Map<String, List<Event>> newPersonsWithEvents)
    {
        peopleEventsMap = newPersonsWithEvents;
    }

    public Map<String, List<Event>> getPeopleEventsMap()
    {
        return peopleEventsMap;
    }

    public Settings getSettings()
    {
        return settings;
    }

    public void setSettings(Settings newSettings)
    {
        settings = newSettings;
    }


    public List<String> getEventTypes()
    {
        return eventTypes;
    }

    public void setEventTypes(List<String> eventTypes)
    {
        this.eventTypes = eventTypes;
    }

    public Map<String, EventColor> getEventColorsMap()
    {
        return eventColorsMap;
    }

    public void setEventColorsMap(Map<String, EventColor> eventColorsMap)
    {
        this.eventColorsMap = eventColorsMap;
    }

    public Person getUsers()
    {
        return person;
    }

    public void setUsers(Person user)
    {
        this.person = user;
    }

    public Set<String> getPaternalAncestors()
    {
        return paternalAncestors;
    }

    public void setPaternalAncestors(Set<String> paternalAncestors)
    {
        this.paternalAncestors = paternalAncestors;
    }

    public Set<String> getMaternalAncestors()
    {
        return maternalAncestors;
    }

    public void setMaternalAncestors(Set<String> maternalAncestors)
    {
        this.maternalAncestors = maternalAncestors;
    }

    public Map<String, Person> getChildrenMap()
    {
        return childrenMap;
    }

    public void setChildrenMap(Map<String, Person> childrenMap)
    {
        this.childrenMap = childrenMap;
    }

    public String getServerHost()
    {
        return serverHost;
    }

    public void setServerHost(String newServer)
    {
        serverHost = newServer;
    }

    public String getIp()
    {
        return ip;
    }

    public void setIp(String newIP)
    {
        ip = newIP;
    }

    public void setAuthToken(String newAuth)
    {
        authToken = newAuth;
    }

    public String getAuthToken()
    {
        return authToken;
    }

    public Person getChosenPerson()
    {
        return chosenPerson;
    }

    public void setChosenPerson(Person chosenPerson)
    {
        this.chosenPerson = chosenPerson;
    }

    public Event getChosenEvent()
    {
        return chosenEvent;
    }

    public void setChosenEvent(Event chosenEvent)
    {
        this.chosenEvent = chosenEvent;
    }



}
