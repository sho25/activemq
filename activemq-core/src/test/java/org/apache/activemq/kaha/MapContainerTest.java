begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
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
name|Collection
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
name|LinkedList
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
name|java
operator|.
name|util
operator|.
name|Map
operator|.
name|Entry
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
name|MapContainer
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
name|Store
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
name|StoreFactory
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

begin_class
specifier|public
class|class
name|MapContainerTest
extends|extends
name|TestCase
block|{
specifier|protected
name|String
name|name
init|=
literal|"test"
decl_stmt|;
specifier|protected
name|Store
name|store
decl_stmt|;
specifier|protected
name|MapContainer
name|container
decl_stmt|;
specifier|protected
name|Map
name|testMap
decl_stmt|;
specifier|protected
specifier|static
specifier|final
name|int
name|COUNT
init|=
literal|10
decl_stmt|;
comment|/*      * Test method for 'org.apache.activemq.kaha.MapContainer.size()'      */
specifier|public
name|void
name|testSize
parameter_list|()
throws|throws
name|Exception
block|{
name|container
operator|.
name|putAll
argument_list|(
name|testMap
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|container
operator|.
name|size
argument_list|()
operator|==
name|testMap
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/*      * Test method for 'org.apache.activemq.kaha.MapContainer.isEmpty()'      */
specifier|public
name|void
name|testIsEmpty
parameter_list|()
throws|throws
name|Exception
block|{
name|assertTrue
argument_list|(
name|container
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/*      * Test method for 'org.apache.activemq.kaha.MapContainer.clear()'      */
specifier|public
name|void
name|testClear
parameter_list|()
throws|throws
name|Exception
block|{
name|container
operator|.
name|putAll
argument_list|(
name|testMap
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|container
operator|.
name|size
argument_list|()
operator|==
name|testMap
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|container
operator|.
name|clear
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|container
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/*      * Test method for 'org.apache.activemq.kaha.MapContainer.containsKey(Object)'      */
specifier|public
name|void
name|testContainsKeyObject
parameter_list|()
throws|throws
name|Exception
block|{
name|container
operator|.
name|putAll
argument_list|(
name|testMap
argument_list|)
expr_stmt|;
for|for
control|(
name|Iterator
name|i
init|=
name|testMap
operator|.
name|entrySet
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
name|Map
operator|.
name|Entry
name|entry
init|=
operator|(
name|Entry
operator|)
name|i
operator|.
name|next
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|container
operator|.
name|containsKey
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
comment|/*      * Test method for 'org.apache.activemq.kaha.MapContainer.get(Object)'      */
specifier|public
name|void
name|testGetObject
parameter_list|()
throws|throws
name|Exception
block|{
name|container
operator|.
name|putAll
argument_list|(
name|testMap
argument_list|)
expr_stmt|;
for|for
control|(
name|Iterator
name|i
init|=
name|testMap
operator|.
name|entrySet
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
name|Map
operator|.
name|Entry
name|entry
init|=
operator|(
name|Entry
operator|)
name|i
operator|.
name|next
argument_list|()
decl_stmt|;
name|Object
name|value
init|=
name|container
operator|.
name|get
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|value
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|value
operator|.
name|equals
argument_list|(
name|entry
operator|.
name|getValue
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
comment|/*      * Test method for 'org.apache.activemq.kaha.MapContainer.containsValue(Object)'      */
specifier|public
name|void
name|testContainsValueObject
parameter_list|()
throws|throws
name|Exception
block|{
name|container
operator|.
name|putAll
argument_list|(
name|testMap
argument_list|)
expr_stmt|;
for|for
control|(
name|Iterator
name|i
init|=
name|testMap
operator|.
name|entrySet
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
name|Map
operator|.
name|Entry
name|entry
init|=
operator|(
name|Entry
operator|)
name|i
operator|.
name|next
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|container
operator|.
name|containsValue
argument_list|(
name|entry
operator|.
name|getValue
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
comment|/*      * Test method for 'org.apache.activemq.kaha.MapContainer.putAll(Map)'      */
specifier|public
name|void
name|testPutAllMap
parameter_list|()
throws|throws
name|Exception
block|{
name|container
operator|.
name|putAll
argument_list|(
name|testMap
argument_list|)
expr_stmt|;
for|for
control|(
name|Iterator
name|i
init|=
name|testMap
operator|.
name|entrySet
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
name|Map
operator|.
name|Entry
name|entry
init|=
operator|(
name|Entry
operator|)
name|i
operator|.
name|next
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|container
operator|.
name|containsValue
argument_list|(
name|entry
operator|.
name|getValue
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|container
operator|.
name|containsKey
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
comment|/*      * Test method for 'org.apache.activemq.kaha.MapContainer.keySet()'      */
specifier|public
name|void
name|testKeySet
parameter_list|()
throws|throws
name|Exception
block|{
name|container
operator|.
name|putAll
argument_list|(
name|testMap
argument_list|)
expr_stmt|;
name|Set
name|keys
init|=
name|container
operator|.
name|keySet
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|keys
operator|.
name|size
argument_list|()
operator|==
name|testMap
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
name|assertTrue
argument_list|(
name|keys
operator|.
name|contains
argument_list|(
name|key
argument_list|)
argument_list|)
expr_stmt|;
name|keys
operator|.
name|remove
argument_list|(
name|key
argument_list|)
expr_stmt|;
block|}
name|assertTrue
argument_list|(
name|container
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/*      * Test method for 'org.apache.activemq.kaha.MapContainer.values()'      */
specifier|public
name|void
name|testValues
parameter_list|()
throws|throws
name|Exception
block|{
name|container
operator|.
name|putAll
argument_list|(
name|testMap
argument_list|)
expr_stmt|;
name|Collection
name|values
init|=
name|container
operator|.
name|values
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|values
operator|.
name|size
argument_list|()
operator|==
name|testMap
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
name|testMap
operator|.
name|values
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
name|value
init|=
name|i
operator|.
name|next
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|values
operator|.
name|contains
argument_list|(
name|value
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|values
operator|.
name|remove
argument_list|(
name|value
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|assertTrue
argument_list|(
name|container
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/*      * Test method for 'org.apache.activemq.kaha.MapContainer.entrySet()'      */
specifier|public
name|void
name|testEntrySet
parameter_list|()
throws|throws
name|Exception
block|{
name|container
operator|.
name|putAll
argument_list|(
name|testMap
argument_list|)
expr_stmt|;
name|Set
name|entries
init|=
name|container
operator|.
name|entrySet
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|entries
operator|.
name|size
argument_list|()
operator|==
name|testMap
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
name|entries
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
name|Map
operator|.
name|Entry
name|entry
init|=
operator|(
name|Entry
operator|)
name|i
operator|.
name|next
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|testMap
operator|.
name|containsKey
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|testMap
operator|.
name|containsValue
argument_list|(
name|entry
operator|.
name|getValue
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
comment|/*      * Test method for 'org.apache.activemq.kaha.MapContainer.remove(Object)'      */
specifier|public
name|void
name|testRemoveObject
parameter_list|()
throws|throws
name|Exception
block|{
name|container
operator|.
name|putAll
argument_list|(
name|testMap
argument_list|)
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
name|container
operator|.
name|remove
argument_list|(
name|i
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|assertTrue
argument_list|(
name|container
operator|.
name|isEmpty
argument_list|()
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
name|store
operator|=
name|getStore
argument_list|()
expr_stmt|;
name|store
operator|.
name|deleteListContainer
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|container
operator|=
name|store
operator|.
name|getMapContainer
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|container
operator|.
name|load
argument_list|()
expr_stmt|;
name|testMap
operator|=
operator|new
name|HashMap
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
name|COUNT
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
name|assertTrue
argument_list|(
name|StoreFactory
operator|.
name|delete
argument_list|(
name|name
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

