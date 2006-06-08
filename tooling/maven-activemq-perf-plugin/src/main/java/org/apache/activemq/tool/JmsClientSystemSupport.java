begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  *  * Copyright 2005-2006 The Apache Software Foundation  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
package|package
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|tool
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|logging
operator|.
name|Log
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|logging
operator|.
name|LogFactory
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Properties
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

begin_class
specifier|public
specifier|abstract
class|class
name|JmsClientSystemSupport
block|{
specifier|private
specifier|static
specifier|final
name|Log
name|log
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|JmsClientSystemSupport
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|PREFIX_CONFIG_SYSTEM_TEST
init|=
literal|"sysTest."
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|DEST_DISTRO_ALL
init|=
literal|"all"
decl_stmt|;
comment|// Each client will send/receive to all destination;
specifier|public
specifier|static
specifier|final
name|String
name|DEST_DISTRO_EQUAL
init|=
literal|"equal"
decl_stmt|;
comment|// Equally divide the number of destinations to the number of clients
specifier|public
specifier|static
specifier|final
name|String
name|DEST_DISTRO_DIVIDE
init|=
literal|"divide"
decl_stmt|;
comment|// Divide the destination among the clients, even if some have more destination than others
specifier|protected
specifier|static
specifier|final
name|String
name|KEY_CLIENT_DEST_COUNT
init|=
literal|"client.destCount"
decl_stmt|;
specifier|protected
specifier|static
specifier|final
name|String
name|KEY_CLIENT_DEST_INDEX
init|=
literal|"client.destIndex"
decl_stmt|;
specifier|protected
name|Properties
name|sysTestSettings
init|=
operator|new
name|Properties
argument_list|()
decl_stmt|;
specifier|protected
name|Properties
name|samplerSettings
init|=
operator|new
name|Properties
argument_list|()
decl_stmt|;
specifier|protected
name|Properties
name|jmsClientSettings
init|=
operator|new
name|Properties
argument_list|()
decl_stmt|;
specifier|protected
name|ThreadGroup
name|clientThreadGroup
decl_stmt|;
specifier|protected
name|PerfMeasurementTool
name|performanceSampler
decl_stmt|;
specifier|protected
name|String
name|reportDirectory
init|=
literal|""
decl_stmt|;
specifier|protected
name|int
name|numClients
init|=
literal|1
decl_stmt|;
specifier|protected
name|int
name|totalDests
init|=
literal|1
decl_stmt|;
specifier|protected
name|String
name|destDistro
init|=
name|DEST_DISTRO_ALL
decl_stmt|;
specifier|public
name|void
name|runSystemTest
parameter_list|()
block|{
comment|// Create performance sampler
name|performanceSampler
operator|=
operator|new
name|PerfMeasurementTool
argument_list|()
expr_stmt|;
name|performanceSampler
operator|.
name|setSamplerSettings
argument_list|(
name|samplerSettings
argument_list|)
expr_stmt|;
name|PerfReportGenerator
name|report
init|=
operator|new
name|PerfReportGenerator
argument_list|()
decl_stmt|;
name|report
operator|.
name|setReportName
argument_list|(
name|this
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|report
operator|.
name|setTestSettings
argument_list|(
name|getSettings
argument_list|()
argument_list|)
expr_stmt|;
name|report
operator|.
name|startGenerateReport
argument_list|()
expr_stmt|;
name|performanceSampler
operator|.
name|setWriter
argument_list|(
name|report
operator|.
name|getWriter
argument_list|()
argument_list|)
expr_stmt|;
name|clientThreadGroup
operator|=
operator|new
name|ThreadGroup
argument_list|(
name|getThreadGroupName
argument_list|()
argument_list|)
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
name|getNumClients
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
specifier|final
name|Properties
name|clientSettings
init|=
operator|new
name|Properties
argument_list|()
decl_stmt|;
name|clientSettings
operator|.
name|putAll
argument_list|(
name|getJmsClientSettings
argument_list|()
argument_list|)
expr_stmt|;
name|distributeDestinations
argument_list|(
name|getDestDistro
argument_list|()
argument_list|,
name|i
argument_list|,
name|getNumClients
argument_list|()
argument_list|,
name|getTotalDests
argument_list|()
argument_list|,
name|clientSettings
argument_list|)
expr_stmt|;
specifier|final
name|String
name|clientName
init|=
name|getClientName
argument_list|()
operator|+
name|i
decl_stmt|;
name|Thread
name|t
init|=
operator|new
name|Thread
argument_list|(
name|clientThreadGroup
argument_list|,
operator|new
name|Runnable
argument_list|()
block|{
specifier|public
name|void
name|run
parameter_list|()
block|{
name|runJmsClient
argument_list|(
name|clientName
argument_list|,
name|clientSettings
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
decl_stmt|;
name|t
operator|.
name|setName
argument_list|(
name|getThreadName
argument_list|()
operator|+
name|i
argument_list|)
expr_stmt|;
name|t
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
name|performanceSampler
operator|.
name|startSampler
argument_list|()
expr_stmt|;
name|performanceSampler
operator|.
name|waitForSamplerToFinish
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|report
operator|.
name|stopGenerateReport
argument_list|()
expr_stmt|;
block|}
specifier|protected
name|void
name|distributeDestinations
parameter_list|(
name|String
name|distroType
parameter_list|,
name|int
name|clientIndex
parameter_list|,
name|int
name|numClients
parameter_list|,
name|int
name|numDests
parameter_list|,
name|Properties
name|clientSettings
parameter_list|)
block|{
if|if
condition|(
name|distroType
operator|.
name|equalsIgnoreCase
argument_list|(
name|DEST_DISTRO_ALL
argument_list|)
condition|)
block|{
name|clientSettings
operator|.
name|setProperty
argument_list|(
name|KEY_CLIENT_DEST_COUNT
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
name|numDests
argument_list|)
argument_list|)
expr_stmt|;
name|clientSettings
operator|.
name|setProperty
argument_list|(
name|KEY_CLIENT_DEST_INDEX
argument_list|,
literal|"0"
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|distroType
operator|.
name|equalsIgnoreCase
argument_list|(
name|DEST_DISTRO_EQUAL
argument_list|)
condition|)
block|{
name|int
name|destPerClient
init|=
operator|(
name|numDests
operator|/
name|numClients
operator|)
decl_stmt|;
comment|// There are equal or more destinations per client
if|if
condition|(
name|destPerClient
operator|>
literal|0
condition|)
block|{
name|clientSettings
operator|.
name|setProperty
argument_list|(
name|KEY_CLIENT_DEST_COUNT
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
name|destPerClient
argument_list|)
argument_list|)
expr_stmt|;
name|clientSettings
operator|.
name|setProperty
argument_list|(
name|KEY_CLIENT_DEST_INDEX
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
name|destPerClient
operator|*
name|clientIndex
argument_list|)
argument_list|)
expr_stmt|;
comment|// If there are more clients than destinations, share destinations per client
block|}
else|else
block|{
name|clientSettings
operator|.
name|setProperty
argument_list|(
name|KEY_CLIENT_DEST_COUNT
argument_list|,
literal|"1"
argument_list|)
expr_stmt|;
comment|// At most one destination per client
name|clientSettings
operator|.
name|setProperty
argument_list|(
name|KEY_CLIENT_DEST_INDEX
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
name|clientIndex
operator|%
name|numDests
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
name|distroType
operator|.
name|equalsIgnoreCase
argument_list|(
name|DEST_DISTRO_DIVIDE
argument_list|)
condition|)
block|{
name|int
name|destPerClient
init|=
operator|(
name|numDests
operator|/
name|numClients
operator|)
decl_stmt|;
comment|// There are equal or more destinations per client
if|if
condition|(
name|destPerClient
operator|>
literal|0
condition|)
block|{
name|int
name|remain
init|=
name|numDests
operator|%
name|numClients
decl_stmt|;
name|int
name|nextIndex
decl_stmt|;
if|if
condition|(
name|clientIndex
operator|<
name|remain
condition|)
block|{
name|destPerClient
operator|++
expr_stmt|;
name|nextIndex
operator|=
name|clientIndex
operator|*
name|destPerClient
expr_stmt|;
block|}
else|else
block|{
name|nextIndex
operator|=
operator|(
name|clientIndex
operator|*
name|destPerClient
operator|)
operator|+
name|remain
expr_stmt|;
block|}
name|clientSettings
operator|.
name|setProperty
argument_list|(
name|KEY_CLIENT_DEST_COUNT
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
name|destPerClient
argument_list|)
argument_list|)
expr_stmt|;
name|clientSettings
operator|.
name|setProperty
argument_list|(
name|KEY_CLIENT_DEST_INDEX
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
name|nextIndex
argument_list|)
argument_list|)
expr_stmt|;
comment|// If there are more clients than destinations, share destinations per client
block|}
else|else
block|{
name|clientSettings
operator|.
name|setProperty
argument_list|(
name|KEY_CLIENT_DEST_COUNT
argument_list|,
literal|"1"
argument_list|)
expr_stmt|;
comment|// At most one destination per client
name|clientSettings
operator|.
name|setProperty
argument_list|(
name|KEY_CLIENT_DEST_INDEX
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
name|clientIndex
operator|%
name|numDests
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// Send to all for unknown behavior
block|}
else|else
block|{
name|clientSettings
operator|.
name|setProperty
argument_list|(
name|KEY_CLIENT_DEST_COUNT
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
name|numDests
argument_list|)
argument_list|)
expr_stmt|;
name|clientSettings
operator|.
name|setProperty
argument_list|(
name|KEY_CLIENT_DEST_INDEX
argument_list|,
literal|"0"
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
specifier|abstract
name|void
name|runJmsClient
parameter_list|(
name|String
name|clientName
parameter_list|,
name|Properties
name|clientSettings
parameter_list|)
function_decl|;
specifier|public
name|String
name|getClientName
parameter_list|()
block|{
return|return
literal|"JMS Client: "
return|;
block|}
specifier|public
name|String
name|getThreadName
parameter_list|()
block|{
return|return
literal|"JMS Client Thread: "
return|;
block|}
specifier|public
name|String
name|getThreadGroupName
parameter_list|()
block|{
return|return
literal|"JMS Clients Thread Group"
return|;
block|}
specifier|public
name|PerfMeasurementTool
name|getPerformanceSampler
parameter_list|()
block|{
return|return
name|performanceSampler
return|;
block|}
specifier|public
name|void
name|setPerformanceSampler
parameter_list|(
name|PerfMeasurementTool
name|performanceSampler
parameter_list|)
block|{
name|this
operator|.
name|performanceSampler
operator|=
name|performanceSampler
expr_stmt|;
block|}
specifier|public
name|Properties
name|getSettings
parameter_list|()
block|{
name|Properties
name|allSettings
init|=
operator|new
name|Properties
argument_list|()
decl_stmt|;
name|allSettings
operator|.
name|putAll
argument_list|(
name|sysTestSettings
argument_list|)
expr_stmt|;
name|allSettings
operator|.
name|putAll
argument_list|(
name|samplerSettings
argument_list|)
expr_stmt|;
name|allSettings
operator|.
name|putAll
argument_list|(
name|jmsClientSettings
argument_list|)
expr_stmt|;
return|return
name|allSettings
return|;
block|}
specifier|public
name|void
name|setSettings
parameter_list|(
name|Properties
name|settings
parameter_list|)
block|{
for|for
control|(
name|Iterator
name|i
init|=
name|settings
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
name|String
name|key
init|=
operator|(
name|String
operator|)
name|i
operator|.
name|next
argument_list|()
decl_stmt|;
name|String
name|val
init|=
name|settings
operator|.
name|getProperty
argument_list|(
name|key
argument_list|)
decl_stmt|;
name|setProperty
argument_list|(
name|key
argument_list|,
name|val
argument_list|)
expr_stmt|;
block|}
name|ReflectionUtil
operator|.
name|configureClass
argument_list|(
name|this
argument_list|,
name|sysTestSettings
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|setProperty
parameter_list|(
name|String
name|key
parameter_list|,
name|String
name|value
parameter_list|)
block|{
if|if
condition|(
name|key
operator|.
name|startsWith
argument_list|(
name|PREFIX_CONFIG_SYSTEM_TEST
argument_list|)
condition|)
block|{
name|sysTestSettings
operator|.
name|setProperty
argument_list|(
name|key
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|key
operator|.
name|startsWith
argument_list|(
name|PerfMeasurementTool
operator|.
name|PREFIX_CONFIG_SYSTEM_TEST
argument_list|)
condition|)
block|{
name|samplerSettings
operator|.
name|setProperty
argument_list|(
name|key
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|jmsClientSettings
operator|.
name|setProperty
argument_list|(
name|key
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|String
name|getReportDirectory
parameter_list|()
block|{
return|return
name|reportDirectory
return|;
block|}
specifier|public
name|void
name|setReportDirectory
parameter_list|(
name|String
name|reportDirectory
parameter_list|)
block|{
name|this
operator|.
name|reportDirectory
operator|=
name|reportDirectory
expr_stmt|;
block|}
specifier|public
name|Properties
name|getSysTestSettings
parameter_list|()
block|{
return|return
name|sysTestSettings
return|;
block|}
specifier|public
name|void
name|setSysTestSettings
parameter_list|(
name|Properties
name|sysTestSettings
parameter_list|)
block|{
name|this
operator|.
name|sysTestSettings
operator|=
name|sysTestSettings
expr_stmt|;
name|ReflectionUtil
operator|.
name|configureClass
argument_list|(
name|this
argument_list|,
name|sysTestSettings
argument_list|)
expr_stmt|;
block|}
specifier|public
name|Properties
name|getSamplerSettings
parameter_list|()
block|{
return|return
name|samplerSettings
return|;
block|}
specifier|public
name|void
name|setSamplerSettings
parameter_list|(
name|Properties
name|samplerSettings
parameter_list|)
block|{
name|this
operator|.
name|samplerSettings
operator|=
name|samplerSettings
expr_stmt|;
block|}
specifier|public
name|Properties
name|getJmsClientSettings
parameter_list|()
block|{
return|return
name|jmsClientSettings
return|;
block|}
specifier|public
name|void
name|setJmsClientSettings
parameter_list|(
name|Properties
name|jmsClientSettings
parameter_list|)
block|{
name|this
operator|.
name|jmsClientSettings
operator|=
name|jmsClientSettings
expr_stmt|;
block|}
specifier|public
name|int
name|getNumClients
parameter_list|()
block|{
return|return
name|numClients
return|;
block|}
specifier|public
name|void
name|setNumClients
parameter_list|(
name|int
name|numClients
parameter_list|)
block|{
name|this
operator|.
name|numClients
operator|=
name|numClients
expr_stmt|;
block|}
specifier|public
name|String
name|getDestDistro
parameter_list|()
block|{
return|return
name|destDistro
return|;
block|}
specifier|public
name|void
name|setDestDistro
parameter_list|(
name|String
name|destDistro
parameter_list|)
block|{
name|this
operator|.
name|destDistro
operator|=
name|destDistro
expr_stmt|;
block|}
specifier|public
name|int
name|getTotalDests
parameter_list|()
block|{
return|return
name|totalDests
return|;
block|}
specifier|public
name|void
name|setTotalDests
parameter_list|(
name|int
name|totalDests
parameter_list|)
block|{
name|this
operator|.
name|totalDests
operator|=
name|totalDests
expr_stmt|;
block|}
block|}
end_class

end_unit

