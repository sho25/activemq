begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  *  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
package|package
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|kaha
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|kaha
operator|.
name|impl
operator|.
name|StoreLockedExcpetion
import|;
end_import

begin_import
import|import
name|junit
operator|.
name|framework
operator|.
name|TestCase
import|;
end_import

begin_comment
comment|/** *Store test *  * @version $Revision: 1.2 $ */
end_comment

begin_class
specifier|public
class|class
name|StoreTest
extends|extends
name|TestCase
block|{
specifier|protected
name|String
name|name
decl_stmt|;
specifier|protected
name|Store
name|store
decl_stmt|;
comment|/*      * Test method for 'org.apache.activemq.kaha.Store.close()'      */
specifier|public
name|void
name|testClose
parameter_list|()
throws|throws
name|Exception
block|{
name|store
operator|.
name|close
argument_list|()
expr_stmt|;
try|try
block|{
comment|//access should throw an exception
name|store
operator|.
name|getListContainer
argument_list|(
literal|"fred"
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Should have got a enception"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{                      }
block|}
comment|/*      * Test method for 'org.apache.activemq.kaha.Store.clear()'      */
specifier|public
name|void
name|testClear
parameter_list|()
throws|throws
name|Exception
block|{
name|int
name|count
init|=
literal|100
decl_stmt|;
name|ListContainer
name|list
init|=
name|store
operator|.
name|getListContainer
argument_list|(
literal|"testClear"
argument_list|)
decl_stmt|;
name|list
operator|.
name|load
argument_list|()
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|count
condition|;
name|i
operator|++
control|)
block|{
name|list
operator|.
name|add
argument_list|(
literal|"test "
operator|+
name|i
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
name|count
argument_list|,
name|list
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|store
operator|.
name|clear
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|list
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/*      * Test method for 'org.apache.activemq.kaha.Store.getMapContainer(Object)'      */
specifier|public
name|void
name|testGetMapContainer
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|containerId
init|=
literal|"test"
decl_stmt|;
name|MapContainer
name|container
init|=
name|store
operator|.
name|getMapContainer
argument_list|(
name|containerId
argument_list|)
decl_stmt|;
name|container
operator|.
name|load
argument_list|()
expr_stmt|;
name|assertNotNull
argument_list|(
name|container
argument_list|)
expr_stmt|;
name|store
operator|.
name|close
argument_list|()
expr_stmt|;
name|store
operator|=
name|getStore
argument_list|()
expr_stmt|;
name|container
operator|=
name|store
operator|.
name|getMapContainer
argument_list|(
name|containerId
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|container
argument_list|)
expr_stmt|;
block|}
comment|/*      * Test method for 'org.apache.activemq.kaha.Store.deleteMapContainer(Object)'      */
specifier|public
name|void
name|testDeleteMapContainer
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|containerId
init|=
literal|"test"
decl_stmt|;
name|MapContainer
name|container
init|=
name|store
operator|.
name|getMapContainer
argument_list|(
name|containerId
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|container
argument_list|)
expr_stmt|;
name|store
operator|.
name|deleteMapContainer
argument_list|(
name|containerId
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|store
operator|.
name|doesMapContainerExist
argument_list|(
name|containerId
argument_list|)
argument_list|)
expr_stmt|;
name|store
operator|.
name|close
argument_list|()
expr_stmt|;
name|store
operator|=
name|getStore
argument_list|()
expr_stmt|;
name|assertFalse
argument_list|(
name|store
operator|.
name|doesMapContainerExist
argument_list|(
name|containerId
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/*      * Test method for 'org.apache.activemq.kaha.Store.getListContainer(Object)'      */
specifier|public
name|void
name|testGetListContainer
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|containerId
init|=
literal|"test"
decl_stmt|;
name|ListContainer
name|container
init|=
name|store
operator|.
name|getListContainer
argument_list|(
name|containerId
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|container
argument_list|)
expr_stmt|;
name|store
operator|.
name|close
argument_list|()
expr_stmt|;
name|store
operator|=
name|getStore
argument_list|()
expr_stmt|;
name|container
operator|=
name|store
operator|.
name|getListContainer
argument_list|(
name|containerId
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|container
argument_list|)
expr_stmt|;
block|}
comment|/*      * Test method for 'org.apache.activemq.kaha.Store.deleteListContainer(Object)'      */
specifier|public
name|void
name|testDeleteListContainer
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|containerId
init|=
literal|"test"
decl_stmt|;
name|ListContainer
name|container
init|=
name|store
operator|.
name|getListContainer
argument_list|(
name|containerId
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|container
argument_list|)
expr_stmt|;
name|store
operator|.
name|deleteListContainer
argument_list|(
name|containerId
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|store
operator|.
name|doesListContainerExist
argument_list|(
name|containerId
argument_list|)
argument_list|)
expr_stmt|;
name|store
operator|.
name|close
argument_list|()
expr_stmt|;
name|store
operator|=
name|getStore
argument_list|()
expr_stmt|;
name|assertFalse
argument_list|(
name|store
operator|.
name|doesListContainerExist
argument_list|(
name|containerId
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testBasicAllocations
parameter_list|()
throws|throws
name|Exception
block|{
name|Map
name|testMap
init|=
operator|new
name|HashMap
argument_list|()
decl_stmt|;
name|int
name|count
init|=
literal|1000
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|count
condition|;
name|i
operator|++
control|)
block|{
name|String
name|key
init|=
literal|"key:"
operator|+
name|i
decl_stmt|;
name|String
name|value
init|=
literal|"value:"
operator|+
name|i
decl_stmt|;
name|testMap
operator|.
name|put
argument_list|(
name|key
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
name|List
name|testList
init|=
operator|new
name|ArrayList
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|count
condition|;
name|i
operator|++
control|)
block|{
name|testList
operator|.
name|add
argument_list|(
literal|"value:"
operator|+
name|i
argument_list|)
expr_stmt|;
block|}
name|String
name|listId
init|=
literal|"testList"
decl_stmt|;
name|String
name|mapId1
init|=
literal|"testMap"
decl_stmt|;
name|String
name|mapId2
init|=
literal|"testMap2"
decl_stmt|;
name|MapContainer
name|mapContainer1
init|=
name|store
operator|.
name|getMapContainer
argument_list|(
name|mapId1
argument_list|)
decl_stmt|;
name|mapContainer1
operator|.
name|load
argument_list|()
expr_stmt|;
name|mapContainer1
operator|.
name|putAll
argument_list|(
name|testMap
argument_list|)
expr_stmt|;
name|MapContainer
name|mapContainer2
init|=
name|store
operator|.
name|getMapContainer
argument_list|(
name|mapId2
argument_list|,
name|mapId2
argument_list|)
decl_stmt|;
name|mapContainer2
operator|.
name|load
argument_list|()
expr_stmt|;
name|mapContainer2
operator|.
name|putAll
argument_list|(
name|testMap
argument_list|)
expr_stmt|;
name|ListContainer
name|listContainer
init|=
name|store
operator|.
name|getListContainer
argument_list|(
name|listId
argument_list|)
decl_stmt|;
name|listContainer
operator|.
name|load
argument_list|()
expr_stmt|;
name|listContainer
operator|.
name|addAll
argument_list|(
name|testList
argument_list|)
expr_stmt|;
name|store
operator|.
name|close
argument_list|()
expr_stmt|;
name|store
operator|=
name|getStore
argument_list|()
expr_stmt|;
name|mapContainer1
operator|=
name|store
operator|.
name|getMapContainer
argument_list|(
name|mapId1
argument_list|)
expr_stmt|;
name|mapContainer1
operator|.
name|load
argument_list|()
expr_stmt|;
name|mapContainer2
operator|=
name|store
operator|.
name|getMapContainer
argument_list|(
name|mapId2
argument_list|,
name|mapId2
argument_list|)
expr_stmt|;
name|mapContainer2
operator|.
name|load
argument_list|()
expr_stmt|;
name|listContainer
operator|=
name|store
operator|.
name|getListContainer
argument_list|(
name|listId
argument_list|)
expr_stmt|;
name|listContainer
operator|.
name|load
argument_list|()
expr_stmt|;
for|for
control|(
name|Iterator
name|i
init|=
name|testMap
operator|.
name|keySet
argument_list|()
operator|.
name|iterator
argument_list|()
init|;
name|i
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|Object
name|key
init|=
name|i
operator|.
name|next
argument_list|()
decl_stmt|;
name|Object
name|value
init|=
name|testMap
operator|.
name|get
argument_list|(
name|key
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|mapContainer1
operator|.
name|containsKey
argument_list|(
name|key
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|value
argument_list|,
name|mapContainer1
operator|.
name|get
argument_list|(
name|key
argument_list|)
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|Iterator
name|i
init|=
name|testMap
operator|.
name|keySet
argument_list|()
operator|.
name|iterator
argument_list|()
init|;
name|i
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|Object
name|key
init|=
name|i
operator|.
name|next
argument_list|()
decl_stmt|;
name|Object
name|value
init|=
name|testMap
operator|.
name|get
argument_list|(
name|key
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|mapContainer2
operator|.
name|containsKey
argument_list|(
name|key
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|value
argument_list|,
name|mapContainer2
operator|.
name|get
argument_list|(
name|key
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
name|testList
operator|.
name|size
argument_list|()
argument_list|,
name|listContainer
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|Iterator
name|i
init|=
name|testList
operator|.
name|iterator
argument_list|()
init|,
name|j
init|=
name|listContainer
operator|.
name|iterator
argument_list|()
init|;
name|i
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|assertEquals
argument_list|(
name|i
operator|.
name|next
argument_list|()
argument_list|,
name|j
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|testLock
parameter_list|()
throws|throws
name|Exception
block|{
name|store
operator|.
name|doesListContainerExist
argument_list|(
literal|"fred"
argument_list|)
expr_stmt|;
name|Store
name|s
init|=
name|getStore
argument_list|()
decl_stmt|;
try|try
block|{
name|s
operator|.
name|doesListContainerExist
argument_list|(
literal|"fred"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|StoreLockedExcpetion
name|e
parameter_list|)
block|{
return|return;
block|}
finally|finally
block|{
name|s
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
name|fail
argument_list|(
literal|"Expected to catch an exception"
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|Store
name|getStore
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|StoreFactory
operator|.
name|open
argument_list|(
name|name
argument_list|,
literal|"rw"
argument_list|)
return|;
block|}
specifier|protected
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|setUp
argument_list|()
expr_stmt|;
name|name
operator|=
name|System
operator|.
name|getProperty
argument_list|(
literal|"basedir"
argument_list|,
literal|"."
argument_list|)
operator|+
literal|"/target/activemq-data/store-test.db"
expr_stmt|;
name|store
operator|=
name|getStore
argument_list|()
expr_stmt|;
block|}
specifier|protected
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|tearDown
argument_list|()
expr_stmt|;
if|if
condition|(
name|store
operator|!=
literal|null
condition|)
block|{
name|store
operator|.
name|close
argument_list|()
expr_stmt|;
name|store
operator|=
literal|null
expr_stmt|;
block|}
name|boolean
name|rc
init|=
name|StoreFactory
operator|.
name|delete
argument_list|(
name|name
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|rc
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

