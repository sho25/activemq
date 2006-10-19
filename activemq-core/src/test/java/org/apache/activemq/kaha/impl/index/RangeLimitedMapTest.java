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
operator|.
name|impl
operator|.
name|index
package|;
end_package

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
comment|/**  * @version $Revision: 1.2 $ */
end_comment

begin_class
specifier|public
class|class
name|RangeLimitedMapTest
extends|extends
name|TestCase
block|{
comment|/**      * @throws java.lang.Exception      * @see junit.framework.TestCase#setUp()      */
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
block|}
comment|/**      * @throws java.lang.Exception      * @see junit.framework.TestCase#tearDown()      */
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
block|}
comment|/**      * Test method for {@link org.apache.activemq.kaha.impl.index.RangeLimitedMap#containsKey(java.lang.Object)}.      */
specifier|public
name|void
name|testContainsKey
parameter_list|()
block|{              }
comment|/**      * Test method for {@link org.apache.activemq.kaha.impl.index.RangeLimitedMap#containsValue(java.lang.Object)}.      */
specifier|public
name|void
name|testContainsValue
parameter_list|()
block|{              }
comment|/**      * Test method for {@link org.apache.activemq.kaha.impl.index.RangeLimitedMap#entrySet()}.      */
specifier|public
name|void
name|testEntrySet
parameter_list|()
block|{            }
comment|/**      * Test method for {@link org.apache.activemq.kaha.impl.index.RangeLimitedMap#get(java.lang.Object)}.      */
specifier|public
name|void
name|testGet
parameter_list|()
block|{              }
comment|/**      * Test method for {@link org.apache.activemq.kaha.impl.index.RangeLimitedMap#isEmpty()}.      */
specifier|public
name|void
name|testIsEmpty
parameter_list|()
block|{              }
comment|/**      * Test method for {@link org.apache.activemq.kaha.impl.index.RangeLimitedMap#keySet()}.      */
specifier|public
name|void
name|testKeySet
parameter_list|()
block|{             }
comment|/**      * Test method for {@link org.apache.activemq.kaha.impl.index.RangeLimitedMap#put(java.lang.Object, java.lang.Object)}.      */
specifier|public
name|void
name|testPut
parameter_list|()
block|{            }
comment|/**      * Test method for {@link org.apache.activemq.kaha.impl.index.RangeLimitedMap#putAll(java.util.Map)}.      */
specifier|public
name|void
name|testPutAll
parameter_list|()
block|{              }
comment|/**      * Test method for {@link org.apache.activemq.kaha.impl.index.RangeLimitedMap#remove(java.lang.Object)}.      */
specifier|public
name|void
name|testRemove
parameter_list|()
block|{              }
comment|/**      * Test method for {@link org.apache.activemq.kaha.impl.index.RangeLimitedMap#size()}.      */
specifier|public
name|void
name|testSize
parameter_list|()
block|{              }
comment|/**      * Test method for {@link org.apache.activemq.kaha.impl.index.RangeLimitedMap#values()}.      */
specifier|public
name|void
name|testValues
parameter_list|()
block|{             }
comment|/**      * Test method for {@link org.apache.activemq.kaha.impl.index.RangeLimitedMap#clear()}.      */
specifier|public
name|void
name|testClear
parameter_list|()
block|{              }
block|}
end_class

end_unit

