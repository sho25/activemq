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
name|filter
package|;
end_package

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
name|junit
operator|.
name|framework
operator|.
name|TestCase
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
name|command
operator|.
name|ActiveMQTempQueue
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
name|command
operator|.
name|ConnectionId
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
name|util
operator|.
name|IdGenerator
import|;
end_import

begin_class
specifier|public
class|class
name|DestinationMapTempDestinationTest
extends|extends
name|TestCase
block|{
specifier|public
name|void
name|testtestTempDestinations
parameter_list|()
throws|throws
name|Exception
block|{
name|ConnectionId
name|id
init|=
operator|new
name|ConnectionId
argument_list|(
operator|new
name|IdGenerator
argument_list|()
operator|.
name|generateId
argument_list|()
argument_list|)
decl_stmt|;
name|DestinationMap
name|map
init|=
operator|new
name|DestinationMap
argument_list|()
decl_stmt|;
name|Object
name|value
init|=
operator|new
name|Object
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
name|ActiveMQTempQueue
name|queue
init|=
operator|new
name|ActiveMQTempQueue
argument_list|(
name|id
argument_list|,
name|i
argument_list|)
decl_stmt|;
name|map
operator|.
name|put
argument_list|(
name|queue
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
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
name|ActiveMQTempQueue
name|queue
init|=
operator|new
name|ActiveMQTempQueue
argument_list|(
name|id
argument_list|,
name|i
argument_list|)
decl_stmt|;
name|map
operator|.
name|remove
argument_list|(
name|queue
argument_list|,
name|value
argument_list|)
expr_stmt|;
name|Set
name|set
init|=
name|map
operator|.
name|get
argument_list|(
name|queue
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|set
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit
