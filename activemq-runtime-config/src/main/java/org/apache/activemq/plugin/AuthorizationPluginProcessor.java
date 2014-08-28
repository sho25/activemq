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
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|filter
operator|.
name|DestinationMapEntry
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
name|security
operator|.
name|*
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
name|schema
operator|.
name|core
operator|.
name|DtoAuthorizationPlugin
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
name|schema
operator|.
name|core
operator|.
name|DtoAuthorizationMap
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
name|schema
operator|.
name|core
operator|.
name|DtoAuthorizationEntry
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
name|List
import|;
end_import

begin_class
specifier|public
class|class
name|AuthorizationPluginProcessor
extends|extends
name|DefaultConfigurationProcessor
block|{
specifier|public
name|AuthorizationPluginProcessor
parameter_list|(
name|RuntimeConfigurationBroker
name|plugin
parameter_list|,
name|Class
name|configurationClass
parameter_list|)
block|{
name|super
argument_list|(
name|plugin
argument_list|,
name|configurationClass
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|modify
parameter_list|(
name|Object
name|existing
parameter_list|,
name|Object
name|candidate
parameter_list|)
block|{
try|try
block|{
comment|// replace authorization map - need exclusive write lock to total broker
name|AuthorizationBroker
name|authorizationBroker
init|=
operator|(
name|AuthorizationBroker
operator|)
name|plugin
operator|.
name|getBrokerService
argument_list|()
operator|.
name|getBroker
argument_list|()
operator|.
name|getAdaptor
argument_list|(
name|AuthorizationBroker
operator|.
name|class
argument_list|)
decl_stmt|;
name|authorizationBroker
operator|.
name|setAuthorizationMap
argument_list|(
name|fromDto
argument_list|(
name|filter
argument_list|(
name|candidate
argument_list|,
name|DtoAuthorizationPlugin
operator|.
name|Map
operator|.
name|class
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|plugin
operator|.
name|info
argument_list|(
literal|"failed to apply modified AuthorizationMap to AuthorizationBroker"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
name|AuthorizationMap
name|fromDto
parameter_list|(
name|List
argument_list|<
name|Object
argument_list|>
name|map
parameter_list|)
block|{
name|XBeanAuthorizationMap
name|xBeanAuthorizationMap
init|=
operator|new
name|XBeanAuthorizationMap
argument_list|()
decl_stmt|;
for|for
control|(
name|Object
name|o
range|:
name|map
control|)
block|{
if|if
condition|(
name|o
operator|instanceof
name|DtoAuthorizationPlugin
operator|.
name|Map
condition|)
block|{
name|DtoAuthorizationPlugin
operator|.
name|Map
name|dtoMap
init|=
operator|(
name|DtoAuthorizationPlugin
operator|.
name|Map
operator|)
name|o
decl_stmt|;
name|List
argument_list|<
name|DestinationMapEntry
argument_list|>
name|entries
init|=
operator|new
name|LinkedList
argument_list|<
name|DestinationMapEntry
argument_list|>
argument_list|()
decl_stmt|;
comment|// revisit - would like to map getAuthorizationMap to generic getContents
for|for
control|(
name|Object
name|authMap
range|:
name|filter
argument_list|(
name|dtoMap
operator|.
name|getAuthorizationMap
argument_list|()
argument_list|,
name|DtoAuthorizationMap
operator|.
name|AuthorizationEntries
operator|.
name|class
argument_list|)
control|)
block|{
for|for
control|(
name|Object
name|entry
range|:
name|filter
argument_list|(
name|getContents
argument_list|(
name|authMap
argument_list|)
argument_list|,
name|DtoAuthorizationEntry
operator|.
name|class
argument_list|)
control|)
block|{
name|entries
operator|.
name|add
argument_list|(
name|fromDto
argument_list|(
name|entry
argument_list|,
operator|new
name|XBeanAuthorizationEntry
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
name|xBeanAuthorizationMap
operator|.
name|setAuthorizationEntries
argument_list|(
name|entries
argument_list|)
expr_stmt|;
try|try
block|{
name|xBeanAuthorizationMap
operator|.
name|afterPropertiesSet
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|plugin
operator|.
name|info
argument_list|(
literal|"failed to update xBeanAuthorizationMap auth entries:"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|Object
name|entry
range|:
name|filter
argument_list|(
name|dtoMap
operator|.
name|getAuthorizationMap
argument_list|()
argument_list|,
name|DtoAuthorizationMap
operator|.
name|TempDestinationAuthorizationEntry
operator|.
name|class
argument_list|)
control|)
block|{
comment|// another restriction - would like to be getContents
name|DtoAuthorizationMap
operator|.
name|TempDestinationAuthorizationEntry
name|dtoEntry
init|=
operator|(
name|DtoAuthorizationMap
operator|.
name|TempDestinationAuthorizationEntry
operator|)
name|entry
decl_stmt|;
name|xBeanAuthorizationMap
operator|.
name|setTempDestinationAuthorizationEntry
argument_list|(
name|fromDto
argument_list|(
name|dtoEntry
operator|.
name|getTempDestinationAuthorizationEntry
argument_list|()
argument_list|,
operator|new
name|TempDestinationAuthorizationEntry
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|plugin
operator|.
name|info
argument_list|(
literal|"No support for updates to: "
operator|+
name|o
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|xBeanAuthorizationMap
return|;
block|}
block|}
end_class

end_unit

