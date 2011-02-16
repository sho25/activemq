begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
package|package
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|util
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|AbstractSet
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
name|Set
import|;
end_import

begin_comment
comment|/**  * A Simple LRU Set  *   *   * @param<K>  * @param<V>  */
end_comment

begin_class
specifier|public
class|class
name|LRUSet
parameter_list|<
name|E
parameter_list|>
extends|extends
name|AbstractSet
argument_list|<
name|E
argument_list|>
implements|implements
name|Set
argument_list|<
name|E
argument_list|>
implements|,
name|Cloneable
implements|,
name|java
operator|.
name|io
operator|.
name|Serializable
block|{
specifier|private
specifier|static
specifier|final
name|Object
name|IGNORE
init|=
operator|new
name|Object
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|LRUCache
name|cache
decl_stmt|;
comment|/**      * Default constructor for an LRU Cache The default capacity is 10000      */
specifier|public
name|LRUSet
parameter_list|()
block|{
name|this
argument_list|(
literal|0
argument_list|,
literal|10000
argument_list|,
literal|0.75f
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
comment|/**      * Constructs a LRUCache with a maximum capacity      *       * @param maximumCacheSize      */
specifier|public
name|LRUSet
parameter_list|(
name|int
name|maximumCacheSize
parameter_list|)
block|{
name|this
argument_list|(
literal|0
argument_list|,
name|maximumCacheSize
argument_list|,
literal|0.75f
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
comment|/**      * Constructs an empty<tt>LRUCache</tt> instance with the specified      * initial capacity, maximumCacheSize,load factor and ordering mode.      *       * @param initialCapacity      *            the initial capacity.      * @param maximumCacheSize      * @param loadFactor      *            the load factor.      * @param accessOrder      *            the ordering mode -<tt>true</tt> for access-order,      *<tt>false</tt> for insertion-order.      * @throws IllegalArgumentException      *             if the initial capacity is negative or the load factor is      *             non-positive.      */
specifier|public
name|LRUSet
parameter_list|(
name|int
name|initialCapacity
parameter_list|,
name|int
name|maximumCacheSize
parameter_list|,
name|float
name|loadFactor
parameter_list|,
name|boolean
name|accessOrder
parameter_list|)
block|{
name|this
operator|.
name|cache
operator|=
operator|new
name|LRUCache
argument_list|<
name|E
argument_list|,
name|Object
argument_list|>
argument_list|(
name|initialCapacity
argument_list|,
name|maximumCacheSize
argument_list|,
name|loadFactor
argument_list|,
name|accessOrder
argument_list|)
expr_stmt|;
block|}
specifier|public
name|Iterator
argument_list|<
name|E
argument_list|>
name|iterator
parameter_list|()
block|{
return|return
name|cache
operator|.
name|keySet
argument_list|()
operator|.
name|iterator
argument_list|()
return|;
block|}
specifier|public
name|int
name|size
parameter_list|()
block|{
return|return
name|cache
operator|.
name|size
argument_list|()
return|;
block|}
specifier|public
name|boolean
name|isEmpty
parameter_list|()
block|{
return|return
name|cache
operator|.
name|isEmpty
argument_list|()
return|;
block|}
specifier|public
name|boolean
name|contains
parameter_list|(
name|Object
name|o
parameter_list|)
block|{
return|return
name|cache
operator|.
name|containsKey
argument_list|(
name|o
argument_list|)
return|;
block|}
specifier|public
name|boolean
name|add
parameter_list|(
name|E
name|o
parameter_list|)
block|{
return|return
name|cache
operator|.
name|put
argument_list|(
name|o
argument_list|,
name|IGNORE
argument_list|)
operator|==
literal|null
return|;
block|}
specifier|public
name|boolean
name|remove
parameter_list|(
name|Object
name|o
parameter_list|)
block|{
return|return
name|cache
operator|.
name|remove
argument_list|(
name|o
argument_list|)
operator|==
name|IGNORE
return|;
block|}
specifier|public
name|void
name|clear
parameter_list|()
block|{
name|cache
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

