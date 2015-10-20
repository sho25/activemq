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
name|plugin
package|;
end_package

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
name|Arrays
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collections
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashSet
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
name|broker
operator|.
name|ConnectionContext
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
name|region
operator|.
name|CompositeDestinationInterceptor
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
name|region
operator|.
name|DestinationInterceptor
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
name|region
operator|.
name|RegionBroker
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
name|region
operator|.
name|virtual
operator|.
name|VirtualDestination
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
name|region
operator|.
name|virtual
operator|.
name|VirtualDestinationInterceptor
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
import|;
end_import

begin_class
specifier|public
specifier|abstract
class|class
name|UpdateVirtualDestinationsTask
implements|implements
name|Runnable
block|{
specifier|public
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|UpdateVirtualDestinationsTask
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|AbstractRuntimeConfigurationBroker
name|plugin
decl_stmt|;
specifier|public
name|UpdateVirtualDestinationsTask
parameter_list|(
name|AbstractRuntimeConfigurationBroker
name|plugin
parameter_list|)
block|{
name|super
argument_list|()
expr_stmt|;
name|this
operator|.
name|plugin
operator|=
name|plugin
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|run
parameter_list|()
block|{
name|boolean
name|updatedExistingInterceptor
init|=
literal|false
decl_stmt|;
name|RegionBroker
name|regionBroker
init|=
operator|(
name|RegionBroker
operator|)
name|plugin
operator|.
name|getBrokerService
argument_list|()
operator|.
name|getRegionBroker
argument_list|()
decl_stmt|;
for|for
control|(
name|DestinationInterceptor
name|destinationInterceptor
range|:
name|plugin
operator|.
name|getBrokerService
argument_list|()
operator|.
name|getDestinationInterceptors
argument_list|()
control|)
block|{
if|if
condition|(
name|destinationInterceptor
operator|instanceof
name|VirtualDestinationInterceptor
condition|)
block|{
comment|// update existing interceptor
specifier|final
name|VirtualDestinationInterceptor
name|virtualDestinationInterceptor
init|=
operator|(
name|VirtualDestinationInterceptor
operator|)
name|destinationInterceptor
decl_stmt|;
name|Set
argument_list|<
name|VirtualDestination
argument_list|>
name|existingVirtualDests
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
name|Collections
operator|.
name|addAll
argument_list|(
name|existingVirtualDests
argument_list|,
name|virtualDestinationInterceptor
operator|.
name|getVirtualDestinations
argument_list|()
argument_list|)
expr_stmt|;
name|Set
argument_list|<
name|VirtualDestination
argument_list|>
name|newVirtualDests
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
name|Collections
operator|.
name|addAll
argument_list|(
name|newVirtualDests
argument_list|,
name|getVirtualDestinations
argument_list|()
argument_list|)
expr_stmt|;
name|Set
argument_list|<
name|VirtualDestination
argument_list|>
name|addedVirtualDests
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
name|Set
argument_list|<
name|VirtualDestination
argument_list|>
name|removedVirtualDests
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
comment|//detect new virtual destinations
for|for
control|(
name|VirtualDestination
name|newVirtualDest
range|:
name|newVirtualDests
control|)
block|{
if|if
condition|(
operator|!
name|existingVirtualDests
operator|.
name|contains
argument_list|(
name|newVirtualDest
argument_list|)
condition|)
block|{
name|addedVirtualDests
operator|.
name|add
argument_list|(
name|newVirtualDest
argument_list|)
expr_stmt|;
block|}
block|}
comment|//detect removed virtual destinations
for|for
control|(
name|VirtualDestination
name|existingVirtualDest
range|:
name|existingVirtualDests
control|)
block|{
if|if
condition|(
operator|!
name|newVirtualDests
operator|.
name|contains
argument_list|(
name|existingVirtualDest
argument_list|)
condition|)
block|{
name|removedVirtualDests
operator|.
name|add
argument_list|(
name|existingVirtualDest
argument_list|)
expr_stmt|;
block|}
block|}
name|virtualDestinationInterceptor
operator|.
name|setVirtualDestinations
argument_list|(
name|getVirtualDestinations
argument_list|()
argument_list|)
expr_stmt|;
name|plugin
operator|.
name|info
argument_list|(
literal|"applied updates to: "
operator|+
name|virtualDestinationInterceptor
argument_list|)
expr_stmt|;
name|updatedExistingInterceptor
operator|=
literal|true
expr_stmt|;
name|ConnectionContext
name|connectionContext
decl_stmt|;
try|try
block|{
name|connectionContext
operator|=
name|plugin
operator|.
name|getBrokerService
argument_list|()
operator|.
name|getAdminConnectionContext
argument_list|()
expr_stmt|;
comment|//signal updates
if|if
condition|(
name|plugin
operator|.
name|getBrokerService
argument_list|()
operator|.
name|isUseVirtualDestSubs
argument_list|()
condition|)
block|{
for|for
control|(
name|VirtualDestination
name|removedVirtualDest
range|:
name|removedVirtualDests
control|)
block|{
name|plugin
operator|.
name|virtualDestinationRemoved
argument_list|(
name|connectionContext
argument_list|,
name|removedVirtualDest
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Removing virtual destination: {}"
argument_list|,
name|removedVirtualDest
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|VirtualDestination
name|addedVirtualDest
range|:
name|addedVirtualDests
control|)
block|{
name|plugin
operator|.
name|virtualDestinationAdded
argument_list|(
name|connectionContext
argument_list|,
name|addedVirtualDest
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Adding virtual destination: {}"
argument_list|,
name|addedVirtualDest
argument_list|)
expr_stmt|;
block|}
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Could not process virtual destination advisories"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
if|if
condition|(
operator|!
name|updatedExistingInterceptor
condition|)
block|{
comment|// add
name|VirtualDestinationInterceptor
name|virtualDestinationInterceptor
init|=
operator|new
name|VirtualDestinationInterceptor
argument_list|()
decl_stmt|;
name|virtualDestinationInterceptor
operator|.
name|setVirtualDestinations
argument_list|(
name|getVirtualDestinations
argument_list|()
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|DestinationInterceptor
argument_list|>
name|interceptorsList
init|=
operator|new
name|ArrayList
argument_list|<
name|DestinationInterceptor
argument_list|>
argument_list|()
decl_stmt|;
name|interceptorsList
operator|.
name|addAll
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|plugin
operator|.
name|getBrokerService
argument_list|()
operator|.
name|getDestinationInterceptors
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|interceptorsList
operator|.
name|add
argument_list|(
name|virtualDestinationInterceptor
argument_list|)
expr_stmt|;
name|DestinationInterceptor
index|[]
name|destinationInterceptors
init|=
name|interceptorsList
operator|.
name|toArray
argument_list|(
operator|new
name|DestinationInterceptor
index|[]
block|{}
argument_list|)
decl_stmt|;
name|plugin
operator|.
name|getBrokerService
argument_list|()
operator|.
name|setDestinationInterceptors
argument_list|(
name|destinationInterceptors
argument_list|)
expr_stmt|;
operator|(
operator|(
name|CompositeDestinationInterceptor
operator|)
name|regionBroker
operator|.
name|getDestinationInterceptor
argument_list|()
operator|)
operator|.
name|setInterceptors
argument_list|(
name|destinationInterceptors
argument_list|)
expr_stmt|;
name|plugin
operator|.
name|info
argument_list|(
literal|"applied new: "
operator|+
name|interceptorsList
argument_list|)
expr_stmt|;
block|}
name|regionBroker
operator|.
name|reapplyInterceptor
argument_list|()
expr_stmt|;
block|}
specifier|protected
specifier|abstract
name|VirtualDestination
index|[]
name|getVirtualDestinations
parameter_list|()
function_decl|;
block|}
end_class

end_unit

