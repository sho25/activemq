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
name|store
operator|.
name|kahadb
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
name|Collection
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
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|broker
operator|.
name|BrokerService
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
name|store
operator|.
name|AbstractVmConcurrentDispatchTest
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|runner
operator|.
name|RunWith
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|runners
operator|.
name|Parameterized
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|runners
operator|.
name|Parameterized
operator|.
name|Parameters
import|;
end_import

begin_class
annotation|@
name|RunWith
argument_list|(
name|Parameterized
operator|.
name|class
argument_list|)
specifier|public
class|class
name|KahaDbVmConcurrentDispatchTest
extends|extends
name|AbstractVmConcurrentDispatchTest
block|{
specifier|private
specifier|final
name|boolean
name|concurrentDispatch
decl_stmt|;
specifier|private
specifier|static
name|boolean
index|[]
name|concurrentDispatchVals
init|=
name|booleanVals
decl_stmt|;
annotation|@
name|Parameters
argument_list|(
name|name
operator|=
literal|"Type:{0}; ReduceMemoryFootPrint:{1}; ConcurrentDispatch:{2}"
argument_list|)
specifier|public
specifier|static
name|Collection
argument_list|<
name|Object
index|[]
argument_list|>
name|data
parameter_list|()
block|{
name|List
argument_list|<
name|Object
index|[]
argument_list|>
name|values
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|MessageType
name|mt
range|:
name|MessageType
operator|.
name|values
argument_list|()
control|)
block|{
for|for
control|(
name|boolean
name|rmfVal
range|:
name|reduceMemoryFootPrintVals
control|)
block|{
for|for
control|(
name|boolean
name|cdVal
range|:
name|concurrentDispatchVals
control|)
block|{
name|values
operator|.
name|add
argument_list|(
operator|new
name|Object
index|[]
block|{
name|mt
block|,
name|rmfVal
block|,
name|cdVal
block|}
argument_list|)
expr_stmt|;
block|}
block|}
block|}
return|return
name|values
return|;
block|}
comment|/**      * @param messageType      * @param reduceMemoryFootPrint      * @param concurrentDispatch      */
specifier|public
name|KahaDbVmConcurrentDispatchTest
parameter_list|(
name|MessageType
name|messageType
parameter_list|,
name|boolean
name|reduceMemoryFootPrint
parameter_list|,
name|boolean
name|concurrentDispatch
parameter_list|)
block|{
name|super
argument_list|(
name|messageType
argument_list|,
name|reduceMemoryFootPrint
argument_list|)
expr_stmt|;
name|this
operator|.
name|concurrentDispatch
operator|=
name|concurrentDispatch
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|void
name|configurePersistenceAdapter
parameter_list|(
name|BrokerService
name|broker
parameter_list|)
throws|throws
name|IOException
block|{
name|KahaDBPersistenceAdapter
name|ad
init|=
operator|(
name|KahaDBPersistenceAdapter
operator|)
name|broker
operator|.
name|getPersistenceAdapter
argument_list|()
decl_stmt|;
name|ad
operator|.
name|setConcurrentStoreAndDispatchQueues
argument_list|(
name|concurrentDispatch
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit
