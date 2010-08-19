/****

    activequant - activestocks.eu

    This program is free software; you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation; either version 2 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License along
    with this program; if not, write to the Free Software Foundation, Inc.,
    51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.

	
	contact  : contact@activestocks.eu
    homepage : http://www.activestocks.eu

****/
package org.activequant.dao;

import java.util.List;

import org.activequant.util.exceptions.DaoException;



/**
 * Regular CRUD functionality.<br>
 * <br>
 * <b>History:</b><br>
 *  - [03.06.2007] Created (Erik Nijkamp)<br>
 *
 *  @author Erik Nijkamp
 */
public interface IDaoSupport<T> {

	/**
	 * Generic method used to get all objects of a particular type.
	 * @return
	 * @throws DaoException
	 */
    T[] findAll() throws DaoException;
    
	/**
	 * Generic method used to get all objects of a particular type matching a referenceObject.
	 * @return
	 * @throws DaoException
	 */
    T[] findAllByExample(T entity) throws DaoException;
    
    /**
     * Generic method used find an object by giving a sample.
     * @return
     */
    T findByExample(T entity) throws DaoException;
    
	/**
	 * Generic method used to get a specific objects based on the ID.
	 * @return
	 * @throws DaoException
	 */
    T find(long id) throws DaoException;
    
    /**
     * Generic method used to save an object (handles both update and insert).
     * @param entity
     * @return
     * @throws DaoException
     */
    T update(T entity) throws DaoException; 
    
    /**
     * Generic method used to remove an object.
     * @param entity
     * @throws DaoException
     */
    void delete(T entity) throws DaoException;
    
    /**
     * Generic method used to remove a set of objects.
     * @param entity
     * @throws DaoException
     */
    void delete(T... entities) throws DaoException;
    
    /**
     * Generic method used to remove a set of objects.
     * @param entity
     * @throws DaoException
     */
    void delete(List<T> entities) throws DaoException;
    
    /**
     * Generic method used to remove all objects of this type.
     * @throws DaoException
     */
    void deleteAll() throws DaoException;
    
    /**
     * Generic method used to count the number of objects.
     * @return
     * @throws DaoException
     */
    int count() throws DaoException;
    
    /**
     * Generic method used to save an object (handles both update and insert).
     * @param entity
     * @return
     * @throws DaoException
     */
    T[] update(T... entities) throws DaoException;
    
    /**
     * Generic method used to save an object (handles both update and insert).
     * @param entity
     * @return
     * @throws DaoException
     */
    List<T> update(List<T> entities) throws DaoException;
}
