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
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|tool
operator|.
name|sampler
operator|.
name|ThroughputSamplerTask
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
name|tool
operator|.
name|sampler
operator|.
name|CpuSamplerTask
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
name|tool
operator|.
name|reports
operator|.
name|PerformanceReportWriter
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
name|tool
operator|.
name|reports
operator|.
name|XmlFilePerfReportWriter
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
name|tool
operator|.
name|reports
operator|.
name|VerbosePerfReportWriter
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
name|tool
operator|.
name|properties
operator|.
name|JmsClientSystemProperties
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
name|tool
operator|.
name|properties
operator|.
name|AbstractObjectProperties
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
name|tool
operator|.
name|properties
operator|.
name|JmsFactoryProperties
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
name|tool
operator|.
name|properties
operator|.
name|ReflectionUtil
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
name|tool
operator|.
name|properties
operator|.
name|JmsClientProperties
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
name|tool
operator|.
name|spi
operator|.
name|SPIConnectionFactory
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|ConnectionFactory
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|JMSException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|ConnectionMetaData
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
name|Enumeration
import|;
end_import

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
name|io
operator|.
name|File
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FileInputStream
import|;
end_import

begin_class
specifier|public
specifier|abstract
class|class
name|AbstractJmsClientSystem
extends|extends
name|AbstractObjectProperties
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
name|AbstractJmsClientSystem
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
name|int
name|clientDestIndex
decl_stmt|,
name|clientDestCount
decl_stmt|;
specifier|protected
name|ThreadGroup
name|clientThreadGroup
decl_stmt|;
specifier|protected
name|ConnectionFactory
name|jmsConnFactory
decl_stmt|;
comment|// Properties
specifier|protected
name|JmsFactoryProperties
name|factory
init|=
operator|new
name|JmsFactoryProperties
argument_list|()
decl_stmt|;
specifier|protected
name|ThroughputSamplerTask
name|tpSampler
init|=
operator|new
name|ThroughputSamplerTask
argument_list|()
decl_stmt|;
specifier|protected
name|CpuSamplerTask
name|cpuSampler
init|=
operator|new
name|CpuSamplerTask
argument_list|()
decl_stmt|;
specifier|public
name|void
name|runSystemTest
parameter_list|()
throws|throws
name|JMSException
block|{
comment|// Create connection factory
name|jmsConnFactory
operator|=
name|loadJmsFactory
argument_list|(
name|getSysTest
argument_list|()
operator|.
name|getSpiClass
argument_list|()
argument_list|,
name|factory
operator|.
name|getFactorySettings
argument_list|()
argument_list|)
expr_stmt|;
name|setProviderMetaData
argument_list|(
name|jmsConnFactory
operator|.
name|createConnection
argument_list|()
operator|.
name|getMetaData
argument_list|()
argument_list|,
name|getJmsClientProperties
argument_list|()
argument_list|)
expr_stmt|;
comment|// Create performance sampler
name|PerformanceReportWriter
name|writer
init|=
name|createPerfWriter
argument_list|()
decl_stmt|;
name|tpSampler
operator|.
name|setPerfReportWriter
argument_list|(
name|writer
argument_list|)
expr_stmt|;
name|cpuSampler
operator|.
name|setPerfReportWriter
argument_list|(
name|writer
argument_list|)
expr_stmt|;
name|writer
operator|.
name|openReportWriter
argument_list|()
expr_stmt|;
name|writer
operator|.
name|writeProperties
argument_list|(
literal|"jvmSettings"
argument_list|,
name|System
operator|.
name|getProperties
argument_list|()
argument_list|)
expr_stmt|;
name|writer
operator|.
name|writeProperties
argument_list|(
literal|"testSystemSettings"
argument_list|,
name|ReflectionUtil
operator|.
name|retrieveObjectProperties
argument_list|(
name|getSysTest
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|writer
operator|.
name|writeProperties
argument_list|(
literal|"jmsFactorySettings"
argument_list|,
name|ReflectionUtil
operator|.
name|retrieveObjectProperties
argument_list|(
name|jmsConnFactory
argument_list|)
argument_list|)
expr_stmt|;
name|writer
operator|.
name|writeProperties
argument_list|(
literal|"jmsClientSettings"
argument_list|,
name|ReflectionUtil
operator|.
name|retrieveObjectProperties
argument_list|(
name|getJmsClientProperties
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|writer
operator|.
name|writeProperties
argument_list|(
literal|"tpSamplerSettings"
argument_list|,
name|ReflectionUtil
operator|.
name|retrieveObjectProperties
argument_list|(
name|tpSampler
argument_list|)
argument_list|)
expr_stmt|;
name|writer
operator|.
name|writeProperties
argument_list|(
literal|"cpuSamplerSettings"
argument_list|,
name|ReflectionUtil
operator|.
name|retrieveObjectProperties
argument_list|(
name|cpuSampler
argument_list|)
argument_list|)
expr_stmt|;
name|clientThreadGroup
operator|=
operator|new
name|ThreadGroup
argument_list|(
name|getSysTest
argument_list|()
operator|.
name|getClientPrefix
argument_list|()
operator|+
literal|" Thread Group"
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
name|getSysTest
argument_list|()
operator|.
name|getNumClients
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|distributeDestinations
argument_list|(
name|getSysTest
argument_list|()
operator|.
name|getDestDistro
argument_list|()
argument_list|,
name|i
argument_list|,
name|getSysTest
argument_list|()
operator|.
name|getNumClients
argument_list|()
argument_list|,
name|getSysTest
argument_list|()
operator|.
name|getTotalDests
argument_list|()
argument_list|)
expr_stmt|;
specifier|final
name|String
name|clientName
init|=
name|getSysTest
argument_list|()
operator|.
name|getClientPrefix
argument_list|()
operator|+
name|i
decl_stmt|;
specifier|final
name|int
name|clientDestIndex
init|=
name|this
operator|.
name|clientDestIndex
decl_stmt|;
specifier|final
name|int
name|clientDestCount
init|=
name|this
operator|.
name|clientDestCount
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
name|clientDestIndex
argument_list|,
name|clientDestCount
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
name|getSysTest
argument_list|()
operator|.
name|getClientPrefix
argument_list|()
operator|+
name|i
operator|+
literal|" Thread"
argument_list|)
expr_stmt|;
name|t
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
comment|// Run samplers
if|if
condition|(
name|getSysTest
argument_list|()
operator|.
name|getSamplers
argument_list|()
operator|.
name|indexOf
argument_list|(
name|JmsClientSystemProperties
operator|.
name|SAMPLER_TP
argument_list|)
operator|>
operator|-
literal|1
condition|)
block|{
name|tpSampler
operator|.
name|startSampler
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|getSysTest
argument_list|()
operator|.
name|getSamplers
argument_list|()
operator|.
name|indexOf
argument_list|(
name|JmsClientSystemProperties
operator|.
name|SAMPLER_CPU
argument_list|)
operator|>
operator|-
literal|1
condition|)
block|{
try|try
block|{
name|cpuSampler
operator|.
name|createPlugin
argument_list|()
expr_stmt|;
name|cpuSampler
operator|.
name|startSampler
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|log
operator|.
name|warn
argument_list|(
literal|"Unable to start CPU sampler plugin. Reason: "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
name|tpSampler
operator|.
name|waitUntilDone
argument_list|()
expr_stmt|;
name|cpuSampler
operator|.
name|waitUntilDone
argument_list|()
expr_stmt|;
name|writer
operator|.
name|closeReportWriter
argument_list|()
expr_stmt|;
block|}
specifier|public
name|ThroughputSamplerTask
name|getTpSampler
parameter_list|()
block|{
return|return
name|tpSampler
return|;
block|}
specifier|public
name|void
name|setTpSampler
parameter_list|(
name|ThroughputSamplerTask
name|tpSampler
parameter_list|)
block|{
name|this
operator|.
name|tpSampler
operator|=
name|tpSampler
expr_stmt|;
block|}
specifier|public
name|CpuSamplerTask
name|getCpuSampler
parameter_list|()
block|{
return|return
name|cpuSampler
return|;
block|}
specifier|public
name|void
name|setCpuSampler
parameter_list|(
name|CpuSamplerTask
name|cpuSampler
parameter_list|)
block|{
name|this
operator|.
name|cpuSampler
operator|=
name|cpuSampler
expr_stmt|;
block|}
specifier|public
name|JmsFactoryProperties
name|getFactory
parameter_list|()
block|{
return|return
name|factory
return|;
block|}
specifier|public
name|void
name|setFactory
parameter_list|(
name|JmsFactoryProperties
name|factory
parameter_list|)
block|{
name|this
operator|.
name|factory
operator|=
name|factory
expr_stmt|;
block|}
specifier|public
specifier|abstract
name|JmsClientSystemProperties
name|getSysTest
parameter_list|()
function_decl|;
specifier|public
specifier|abstract
name|void
name|setSysTest
parameter_list|(
name|JmsClientSystemProperties
name|sysTestProps
parameter_list|)
function_decl|;
specifier|public
specifier|abstract
name|JmsClientProperties
name|getJmsClientProperties
parameter_list|()
function_decl|;
specifier|protected
name|PerformanceReportWriter
name|createPerfWriter
parameter_list|()
block|{
if|if
condition|(
name|getSysTest
argument_list|()
operator|.
name|getReportType
argument_list|()
operator|.
name|equalsIgnoreCase
argument_list|(
name|JmsClientSystemProperties
operator|.
name|REPORT_XML_FILE
argument_list|)
condition|)
block|{
name|String
name|reportName
decl_stmt|;
if|if
condition|(
operator|(
name|reportName
operator|=
name|getSysTest
argument_list|()
operator|.
name|getReportName
argument_list|()
operator|)
operator|==
literal|null
condition|)
block|{
name|reportName
operator|=
name|getSysTest
argument_list|()
operator|.
name|getClientPrefix
argument_list|()
operator|+
literal|"_"
operator|+
literal|"numClients"
operator|+
name|getSysTest
argument_list|()
operator|.
name|getNumClients
argument_list|()
operator|+
literal|"_"
operator|+
literal|"numDests"
operator|+
name|getSysTest
argument_list|()
operator|.
name|getTotalDests
argument_list|()
operator|+
literal|"_"
operator|+
name|getSysTest
argument_list|()
operator|.
name|getDestDistro
argument_list|()
expr_stmt|;
block|}
return|return
operator|new
name|XmlFilePerfReportWriter
argument_list|(
name|getSysTest
argument_list|()
operator|.
name|getReportDir
argument_list|()
argument_list|,
name|reportName
argument_list|)
return|;
block|}
elseif|else
if|if
condition|(
name|getSysTest
argument_list|()
operator|.
name|getReportType
argument_list|()
operator|.
name|equalsIgnoreCase
argument_list|(
name|JmsClientSystemProperties
operator|.
name|REPORT_VERBOSE
argument_list|)
condition|)
block|{
return|return
operator|new
name|VerbosePerfReportWriter
argument_list|()
return|;
block|}
else|else
block|{
comment|// Use verbose if unknown report type
return|return
operator|new
name|VerbosePerfReportWriter
argument_list|()
return|;
block|}
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
parameter_list|)
block|{
if|if
condition|(
name|distroType
operator|.
name|equalsIgnoreCase
argument_list|(
name|JmsClientSystemProperties
operator|.
name|DEST_DISTRO_ALL
argument_list|)
condition|)
block|{
name|clientDestCount
operator|=
name|numDests
expr_stmt|;
name|clientDestIndex
operator|=
literal|0
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|distroType
operator|.
name|equalsIgnoreCase
argument_list|(
name|JmsClientSystemProperties
operator|.
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
name|clientDestCount
operator|=
name|destPerClient
expr_stmt|;
name|clientDestIndex
operator|=
name|destPerClient
operator|*
name|clientIndex
expr_stmt|;
comment|// If there are more clients than destinations, share destinations per client
block|}
else|else
block|{
name|clientDestCount
operator|=
literal|1
expr_stmt|;
comment|// At most one destination per client
name|clientDestIndex
operator|=
name|clientIndex
operator|%
name|numDests
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
name|JmsClientSystemProperties
operator|.
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
name|clientDestCount
operator|=
name|destPerClient
expr_stmt|;
name|clientDestIndex
operator|=
name|nextIndex
expr_stmt|;
comment|// If there are more clients than destinations, share destinations per client
block|}
else|else
block|{
name|clientDestCount
operator|=
literal|1
expr_stmt|;
comment|// At most one destination per client
name|clientDestIndex
operator|=
name|clientIndex
operator|%
name|numDests
expr_stmt|;
block|}
comment|// Send to all for unknown behavior
block|}
else|else
block|{
name|log
operator|.
name|warn
argument_list|(
literal|"Unknown destination distribution type: "
operator|+
name|distroType
argument_list|)
expr_stmt|;
name|clientDestCount
operator|=
name|numDests
expr_stmt|;
name|clientDestIndex
operator|=
literal|0
expr_stmt|;
block|}
block|}
specifier|protected
name|ConnectionFactory
name|loadJmsFactory
parameter_list|(
name|String
name|spiClass
parameter_list|,
name|Properties
name|factorySettings
parameter_list|)
throws|throws
name|JMSException
block|{
try|try
block|{
name|Class
name|spi
init|=
name|Class
operator|.
name|forName
argument_list|(
name|spiClass
argument_list|)
decl_stmt|;
name|SPIConnectionFactory
name|spiFactory
init|=
operator|(
name|SPIConnectionFactory
operator|)
name|spi
operator|.
name|newInstance
argument_list|()
decl_stmt|;
name|ConnectionFactory
name|jmsFactory
init|=
name|spiFactory
operator|.
name|createConnectionFactory
argument_list|(
name|factorySettings
argument_list|)
decl_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"Created: "
operator|+
name|jmsFactory
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
operator|+
literal|" using SPIConnectionFactory: "
operator|+
name|spiFactory
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|jmsFactory
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
throw|throw
operator|new
name|JMSException
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
throw|;
block|}
block|}
specifier|protected
name|void
name|setProviderMetaData
parameter_list|(
name|ConnectionMetaData
name|metaData
parameter_list|,
name|JmsClientProperties
name|props
parameter_list|)
throws|throws
name|JMSException
block|{
name|props
operator|.
name|setJmsProvider
argument_list|(
name|metaData
operator|.
name|getJMSProviderName
argument_list|()
operator|+
literal|"-"
operator|+
name|metaData
operator|.
name|getProviderVersion
argument_list|()
argument_list|)
expr_stmt|;
name|props
operator|.
name|setJmsVersion
argument_list|(
name|metaData
operator|.
name|getJMSVersion
argument_list|()
argument_list|)
expr_stmt|;
name|String
name|jmsProperties
init|=
literal|""
decl_stmt|;
name|Enumeration
name|jmsProps
init|=
name|metaData
operator|.
name|getJMSXPropertyNames
argument_list|()
decl_stmt|;
while|while
condition|(
name|jmsProps
operator|.
name|hasMoreElements
argument_list|()
condition|)
block|{
name|jmsProperties
operator|+=
operator|(
name|jmsProps
operator|.
name|nextElement
argument_list|()
operator|.
name|toString
argument_list|()
operator|+
literal|","
operator|)
expr_stmt|;
block|}
if|if
condition|(
name|jmsProperties
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
comment|// Remove the last comma
name|jmsProperties
operator|=
name|jmsProperties
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|jmsProperties
operator|.
name|length
argument_list|()
operator|-
literal|1
argument_list|)
expr_stmt|;
block|}
name|props
operator|.
name|setJmsProperties
argument_list|(
name|jmsProperties
argument_list|)
expr_stmt|;
block|}
specifier|protected
specifier|abstract
name|void
name|runJmsClient
parameter_list|(
name|String
name|clientName
parameter_list|,
name|int
name|clientDestIndex
parameter_list|,
name|int
name|clientDestCount
parameter_list|)
function_decl|;
specifier|protected
specifier|static
name|Properties
name|parseStringArgs
parameter_list|(
name|String
index|[]
name|args
parameter_list|)
block|{
name|File
name|configFile
init|=
literal|null
decl_stmt|;
name|Properties
name|props
init|=
operator|new
name|Properties
argument_list|()
decl_stmt|;
if|if
condition|(
name|args
operator|==
literal|null
operator|||
name|args
operator|.
name|length
operator|==
literal|0
condition|)
block|{
return|return
name|props
return|;
comment|// Empty properties
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
name|args
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|String
name|arg
init|=
name|args
index|[
name|i
index|]
decl_stmt|;
if|if
condition|(
name|arg
operator|.
name|startsWith
argument_list|(
literal|"-D"
argument_list|)
operator|||
name|arg
operator|.
name|startsWith
argument_list|(
literal|"-d"
argument_list|)
condition|)
block|{
name|arg
operator|=
name|arg
operator|.
name|substring
argument_list|(
literal|2
argument_list|)
expr_stmt|;
block|}
name|int
name|index
init|=
name|arg
operator|.
name|indexOf
argument_list|(
literal|"="
argument_list|)
decl_stmt|;
name|String
name|key
init|=
name|arg
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|index
argument_list|)
decl_stmt|;
name|String
name|val
init|=
name|arg
operator|.
name|substring
argument_list|(
name|index
operator|+
literal|1
argument_list|)
decl_stmt|;
if|if
condition|(
name|key
operator|.
name|equalsIgnoreCase
argument_list|(
literal|"sysTest.propsConfigFile"
argument_list|)
condition|)
block|{
if|if
condition|(
operator|!
name|val
operator|.
name|endsWith
argument_list|(
literal|".properties"
argument_list|)
condition|)
block|{
name|val
operator|+=
literal|".properties"
expr_stmt|;
block|}
name|configFile
operator|=
operator|new
name|File
argument_list|(
name|val
argument_list|)
expr_stmt|;
block|}
name|props
operator|.
name|setProperty
argument_list|(
name|key
argument_list|,
name|val
argument_list|)
expr_stmt|;
block|}
name|Properties
name|fileProps
init|=
operator|new
name|Properties
argument_list|()
decl_stmt|;
try|try
block|{
if|if
condition|(
name|configFile
operator|!=
literal|null
condition|)
block|{
name|fileProps
operator|.
name|load
argument_list|(
operator|new
name|FileInputStream
argument_list|(
name|configFile
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
comment|// Overwrite file settings with command line settings
name|fileProps
operator|.
name|putAll
argument_list|(
name|props
argument_list|)
expr_stmt|;
return|return
name|fileProps
return|;
block|}
block|}
end_class

end_unit

