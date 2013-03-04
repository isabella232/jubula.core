/**
 * 
 */
package org.eclipse.jubula.client.core.utils;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jubula.client.core.events.DataChangedEvent;
import org.eclipse.jubula.client.core.events.DataEventDispatcher;
import org.eclipse.jubula.client.core.events.DataEventDispatcher.IDataChangedListener;
import org.eclipse.jubula.client.core.events.DataEventDispatcher.IProjectLoadedListener;

/**
 * 
 * @author al
 *
 * This is basically a Map used as a cache which supports 
 * invalidation based on predefined events.
 * 
 * @param <TKey> the type of the key component
 * @param <TValue> the type of the value component
 */
public class ControlledCache<TKey, TValue> implements IDataChangedListener,
        IProjectLoadedListener {
    
    /** List of data changed events supported for cache invalidation */
    public enum ControlTypes {
        /** project was (re)loaded */
        PROJECT_LOADED(1), 
        /** some data changed */
        DATA_CHANGED(2);

        /** value of the enum */
        private long m_flag;

        /** set values for the different types 
         * @param flagPosition the bitwise position of the 1 in a long to
         * allow or-ing together several flags.
         */
        ControlTypes(int flagPosition) {
            Assert.isTrue(flagPosition > 0, "postion out of range 1-60"); //$NON-NLS-1$
            Assert.isTrue(flagPosition < 60, "postion out of range 1-60"); //$NON-NLS-1$
            m_flag = 1 << flagPosition;
        }
        
        /**
         * @return the value of the enum
         */
        public long getFlag() {
            return m_flag;
        }
    }
    
    /** cache storage */
    private Map<TKey, TValue> m_cache;
    
    /** 
     * @see ControlledCache#ControlledCache(long, int)
     * @param controlledBy the events which will invalidate the cache
     */
    public ControlledCache(ControlTypes ... controlledBy) {
        this(17, controlledBy);
    }
    
    /**
     * @param controlledByList the events which will invalidate the cache
     * @param size the initial size of the Map
     */
    public ControlledCache(int size, ControlTypes ... controlledByList) {
        m_cache = new HashMap<TKey, TValue>(size);
        
        for (ControlTypes controlledBy : controlledByList) {
            registerHandler(controlledBy);
        }
    }
    
    /**
     * store some data in the cache
     * @param key Key into the underlying map
     * @param value data to be stored
     * @return @see {@link Map}
     */
    public TValue add(TKey key, TValue value) {
        return m_cache.put(key, value);
    }
    
    /**
     * fetch data from the cache
     * @param key Key into the underlying map
     * @return @see {@link Map}
     */
    public TValue get(TKey key) {
        return m_cache.get(key);
    }
    
    /**
     * remove the data from the cache
     * @param key Key into the underlying map
     * @return @see {@link Map}
     */
    public TValue remove(TKey key) {
        return m_cache.remove(key);
    }
    
    /**
     * register the event handler for this cache
     * @param controlledBy the events which will invalidate the cache
     */
    private void registerHandler(ControlTypes controlledBy) {
        if (controlledBy == ControlTypes.PROJECT_LOADED) {
            DataEventDispatcher.getInstance().addProjectLoadedListener(this,
                    true);
        }
        if (controlledBy == ControlTypes.DATA_CHANGED) {
            DataEventDispatcher.getInstance()
                    .addDataChangedListener(this, true);
        }
    }

    /**
     * @see org.eclipse.jubula.client.core.events.DataEventDispatcher.IProjectLoadedListener#handleProjectLoaded()
     */
    public void handleProjectLoaded() {
        m_cache.clear();
    }

    /**
     * @param events Possible events for this handler
     * @see org.eclipse.jubula.client.core.events.DataEventDispatcher.IDataChangedListener#handleDataChanged(org.eclipse.jubula.client.core.events.DataChangedEvent[])
     */
    public void handleDataChanged(DataChangedEvent... events) {
        m_cache.clear();

    }

}
