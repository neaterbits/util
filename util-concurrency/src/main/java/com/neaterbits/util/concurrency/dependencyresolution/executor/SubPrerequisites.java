package com.neaterbits.util.concurrency.dependencyresolution.executor;

import java.util.Collection;
import java.util.Objects;

import com.neaterbits.util.coll.MapOfList;

public final class SubPrerequisites<PREREQUISITE> {
    
    private final Class<PREREQUISITE> type;
    private final Collection<PREREQUISITE> list;
    private final MapOfList<PREREQUISITE, PREREQUISITE> mapOfList;

    public SubPrerequisites(Class<PREREQUISITE> type, Collection<PREREQUISITE> list) {
        
        Objects.requireNonNull(type);
        Objects.requireNonNull(list);

        this.type = type;
        this.list = list;
        this.mapOfList = null;
    }

    public SubPrerequisites(Class<PREREQUISITE> type, MapOfList<PREREQUISITE, PREREQUISITE> mapOfList) {
        
        Objects.requireNonNull(type);
        Objects.requireNonNull(mapOfList);

        this.type = type;
        this.mapOfList = mapOfList;
        this.list = null;
    }
    
    boolean isEmpty() {
        
        return     (list != null && list.isEmpty())
                || (mapOfList != null && mapOfList.isEmpty()); 
    }

    Class<PREREQUISITE> getPrerequisiteType() {
        return type;
    }

    Collection<PREREQUISITE> getList() {
        return list;
    }

    MapOfList<PREREQUISITE, PREREQUISITE> getMapOfList() {
        return mapOfList;
    }

    @Override
    public String toString() {
        return "SubPrerequisites [type=" + type + ", list=" + list + ", mapOfList=" + mapOfList + "]";
    }
}
