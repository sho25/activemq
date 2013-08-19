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
name|Collections
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Date
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
name|TreeMap
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|bind
operator|.
name|JAXBContext
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|bind
operator|.
name|JAXBElement
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|bind
operator|.
name|JAXBException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|bind
operator|.
name|Unmarshaller
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|parsers
operator|.
name|DocumentBuilder
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|parsers
operator|.
name|DocumentBuilderFactory
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|parsers
operator|.
name|ParserConfigurationException
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
name|BrokerFilter
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
name|Broker
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
name|NetworkConnector
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
name|spring
operator|.
name|Utils
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
name|IntrospectionSupport
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

begin_import
import|import
name|org
operator|.
name|springframework
operator|.
name|core
operator|.
name|io
operator|.
name|Resource
import|;
end_import

begin_import
import|import
name|org
operator|.
name|w3c
operator|.
name|dom
operator|.
name|Document
import|;
end_import

begin_import
import|import
name|org
operator|.
name|w3c
operator|.
name|dom
operator|.
name|Node
import|;
end_import

begin_import
import|import
name|org
operator|.
name|xml
operator|.
name|sax
operator|.
name|SAXException
import|;
end_import

begin_class
specifier|public
class|class
name|RuntimeConfigurationBroker
extends|extends
name|BrokerFilter
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
name|RuntimeConfigurationBroker
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
name|long
name|checkPeriod
decl_stmt|;
specifier|private
name|long
name|lastModified
init|=
operator|-
literal|1
decl_stmt|;
specifier|private
name|Resource
name|configToMonitor
decl_stmt|;
specifier|private
name|Broker
name|currentConfiguration
decl_stmt|;
specifier|private
name|Runnable
name|monitorTask
decl_stmt|;
specifier|public
name|RuntimeConfigurationBroker
parameter_list|(
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|broker
operator|.
name|Broker
name|next
parameter_list|)
block|{
name|super
argument_list|(
name|next
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|start
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|start
argument_list|()
expr_stmt|;
try|try
block|{
name|configToMonitor
operator|=
name|Utils
operator|.
name|resourceFromString
argument_list|(
name|next
operator|.
name|getBrokerService
argument_list|()
operator|.
name|getConfigurationUrl
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|error
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"failed to determine configuration url resource from broker, updates cannot be tracked"
argument_list|,
name|error
argument_list|)
expr_stmt|;
block|}
name|currentConfiguration
operator|=
name|loadConfiguration
argument_list|(
name|configToMonitor
argument_list|)
expr_stmt|;
name|monitorModification
argument_list|(
name|configToMonitor
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|stop
parameter_list|()
throws|throws
name|Exception
block|{
if|if
condition|(
name|monitorTask
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|this
operator|.
name|getBrokerService
argument_list|()
operator|.
name|getScheduler
argument_list|()
operator|.
name|cancel
argument_list|(
name|monitorTask
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|letsNotStopStop
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Failed to cancel config monitor task"
argument_list|,
name|letsNotStopStop
argument_list|)
expr_stmt|;
block|}
block|}
name|super
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
specifier|private
name|void
name|monitorModification
parameter_list|(
specifier|final
name|Resource
name|configToMonitor
parameter_list|)
block|{
name|Runnable
name|monitorTask
init|=
operator|new
name|Runnable
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|run
parameter_list|()
block|{
try|try
block|{
if|if
condition|(
name|configToMonitor
operator|.
name|lastModified
argument_list|()
operator|>
name|lastModified
condition|)
block|{
name|applyModifications
argument_list|(
name|configToMonitor
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
name|LOG
operator|.
name|error
argument_list|(
literal|"Failed to determine lastModified time on configuration: "
operator|+
name|configToMonitor
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
decl_stmt|;
if|if
condition|(
name|lastModified
operator|>
literal|0
condition|)
block|{
name|this
operator|.
name|getBrokerService
argument_list|()
operator|.
name|getScheduler
argument_list|()
operator|.
name|executePeriodically
argument_list|(
name|monitorTask
argument_list|,
name|checkPeriod
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Monitoring for updates (every "
operator|+
name|checkPeriod
operator|+
literal|"millis) : "
operator|+
name|configToMonitor
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
name|void
name|applyModifications
parameter_list|(
name|Resource
name|configToMonitor
parameter_list|)
block|{
name|Broker
name|changed
init|=
name|loadConfiguration
argument_list|(
name|configToMonitor
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|currentConfiguration
operator|.
name|equals
argument_list|(
name|changed
argument_list|)
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"configuration change in "
operator|+
name|configToMonitor
operator|+
literal|" at: "
operator|+
operator|new
name|Date
argument_list|(
name|lastModified
argument_list|)
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"current:"
operator|+
name|currentConfiguration
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"new    :"
operator|+
name|changed
argument_list|)
expr_stmt|;
name|processNetworkConnectors
argument_list|(
name|currentConfiguration
argument_list|,
name|changed
argument_list|)
expr_stmt|;
name|currentConfiguration
operator|=
name|changed
expr_stmt|;
block|}
else|else
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"file modification but no material change to configuration in "
operator|+
name|configToMonitor
operator|+
literal|" at: "
operator|+
operator|new
name|Date
argument_list|(
name|lastModified
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
name|void
name|processNetworkConnectors
parameter_list|(
name|Broker
name|currentConfiguration
parameter_list|,
name|Broker
name|modifiedConfiguration
parameter_list|)
block|{
name|List
argument_list|<
name|Broker
operator|.
name|NetworkConnectors
argument_list|>
name|currentNCsElems
init|=
name|filterElement
argument_list|(
name|currentConfiguration
operator|.
name|getContents
argument_list|()
argument_list|,
name|Broker
operator|.
name|NetworkConnectors
operator|.
name|class
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|Broker
operator|.
name|NetworkConnectors
argument_list|>
name|modifiedNCsElems
init|=
name|filterElement
argument_list|(
name|modifiedConfiguration
operator|.
name|getContents
argument_list|()
argument_list|,
name|Broker
operator|.
name|NetworkConnectors
operator|.
name|class
argument_list|)
decl_stmt|;
name|int
name|modIndex
init|=
literal|0
decl_stmt|,
name|currentIndex
init|=
literal|0
decl_stmt|;
for|for
control|(
init|;
name|modIndex
operator|<
name|modifiedNCsElems
operator|.
name|size
argument_list|()
operator|&&
name|currentIndex
operator|<
name|currentNCsElems
operator|.
name|size
argument_list|()
condition|;
name|modIndex
operator|++
operator|,
name|currentIndex
operator|++
control|)
block|{
comment|// walk the list of individual nc's...
name|applyModifications
argument_list|(
name|currentNCsElems
operator|.
name|get
argument_list|(
name|currentIndex
argument_list|)
operator|.
name|getContents
argument_list|()
argument_list|,
name|modifiedNCsElems
operator|.
name|get
argument_list|(
name|modIndex
argument_list|)
operator|.
name|getContents
argument_list|()
argument_list|)
expr_stmt|;
block|}
for|for
control|(
init|;
name|modIndex
operator|<
name|modifiedNCsElems
operator|.
name|size
argument_list|()
condition|;
name|modIndex
operator|++
control|)
block|{
comment|// new networkConnectors element; add all
for|for
control|(
name|Object
name|nc
range|:
name|modifiedNCsElems
operator|.
name|get
argument_list|(
name|modIndex
argument_list|)
operator|.
name|getContents
argument_list|()
control|)
block|{
name|addNetworkConnector
argument_list|(
name|nc
argument_list|)
expr_stmt|;
block|}
block|}
for|for
control|(
init|;
name|currentIndex
operator|<
name|currentNCsElems
operator|.
name|size
argument_list|()
condition|;
name|currentIndex
operator|++
control|)
block|{
comment|// removal of networkConnectors element; remove all
for|for
control|(
name|Object
name|nc
range|:
name|modifiedNCsElems
operator|.
name|get
argument_list|(
name|modIndex
argument_list|)
operator|.
name|getContents
argument_list|()
control|)
block|{
name|removeNetworkConnector
argument_list|(
name|nc
argument_list|)
expr_stmt|;
block|}
block|}
block|}
specifier|private
name|void
name|applyModifications
parameter_list|(
name|List
argument_list|<
name|Object
argument_list|>
name|current
parameter_list|,
name|List
argument_list|<
name|Object
argument_list|>
name|modification
parameter_list|)
block|{
name|int
name|modIndex
init|=
literal|0
decl_stmt|,
name|currentIndex
init|=
literal|0
decl_stmt|;
for|for
control|(
init|;
name|modIndex
operator|<
name|modification
operator|.
name|size
argument_list|()
operator|&&
name|currentIndex
operator|<
name|current
operator|.
name|size
argument_list|()
condition|;
name|modIndex
operator|++
operator|,
name|currentIndex
operator|++
control|)
block|{
name|Object
name|currentNc
init|=
name|current
operator|.
name|get
argument_list|(
name|currentIndex
argument_list|)
decl_stmt|;
name|Object
name|candidateNc
init|=
name|modification
operator|.
name|get
argument_list|(
name|modIndex
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|currentNc
operator|.
name|equals
argument_list|(
name|candidateNc
argument_list|)
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"modification to:"
operator|+
name|currentNc
operator|+
literal|" , with: "
operator|+
name|candidateNc
argument_list|)
expr_stmt|;
name|removeNetworkConnector
argument_list|(
name|currentNc
argument_list|)
expr_stmt|;
name|addNetworkConnector
argument_list|(
name|candidateNc
argument_list|)
expr_stmt|;
block|}
block|}
for|for
control|(
init|;
name|modIndex
operator|<
name|modification
operator|.
name|size
argument_list|()
condition|;
name|modIndex
operator|++
control|)
block|{
name|addNetworkConnector
argument_list|(
name|modification
operator|.
name|get
argument_list|(
name|modIndex
argument_list|)
argument_list|)
expr_stmt|;
block|}
for|for
control|(
init|;
name|currentIndex
operator|<
name|current
operator|.
name|size
argument_list|()
condition|;
name|currentIndex
operator|++
control|)
block|{
name|removeNetworkConnector
argument_list|(
name|current
operator|.
name|get
argument_list|(
name|currentIndex
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
name|void
name|removeNetworkConnector
parameter_list|(
name|Object
name|o
parameter_list|)
block|{
if|if
condition|(
name|o
operator|instanceof
name|NetworkConnector
condition|)
block|{
name|NetworkConnector
name|toRemove
init|=
operator|(
name|NetworkConnector
operator|)
name|o
decl_stmt|;
for|for
control|(
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|network
operator|.
name|NetworkConnector
name|existingCandidate
range|:
name|getBrokerService
argument_list|()
operator|.
name|getNetworkConnectors
argument_list|()
control|)
block|{
if|if
condition|(
name|configMatch
argument_list|(
name|toRemove
argument_list|,
name|existingCandidate
argument_list|)
condition|)
block|{
if|if
condition|(
name|getBrokerService
argument_list|()
operator|.
name|removeNetworkConnector
argument_list|(
name|existingCandidate
argument_list|)
condition|)
block|{
try|try
block|{
name|existingCandidate
operator|.
name|stop
argument_list|()
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"stopped and removed networkConnector: "
operator|+
name|existingCandidate
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Failed to stop removed network connector: "
operator|+
name|existingCandidate
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
block|}
specifier|private
name|boolean
name|configMatch
parameter_list|(
name|NetworkConnector
name|dto
parameter_list|,
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|network
operator|.
name|NetworkConnector
name|candidate
parameter_list|)
block|{
name|TreeMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|dtoProps
init|=
operator|new
name|TreeMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|IntrospectionSupport
operator|.
name|getProperties
argument_list|(
name|dto
argument_list|,
name|dtoProps
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|TreeMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|candidateProps
init|=
operator|new
name|TreeMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|IntrospectionSupport
operator|.
name|getProperties
argument_list|(
name|candidate
argument_list|,
name|candidateProps
argument_list|,
literal|null
argument_list|)
expr_stmt|;
comment|// every dto prop must be present in the candidate
for|for
control|(
name|String
name|key
range|:
name|dtoProps
operator|.
name|keySet
argument_list|()
control|)
block|{
if|if
condition|(
operator|!
name|candidateProps
operator|.
name|containsKey
argument_list|(
name|key
argument_list|)
operator|||
operator|!
name|candidateProps
operator|.
name|get
argument_list|(
name|key
argument_list|)
operator|.
name|equals
argument_list|(
name|dtoProps
operator|.
name|get
argument_list|(
name|key
argument_list|)
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
block|}
return|return
literal|true
return|;
block|}
specifier|private
name|void
name|addNetworkConnector
parameter_list|(
name|Object
name|o
parameter_list|)
block|{
if|if
condition|(
name|o
operator|instanceof
name|NetworkConnector
condition|)
block|{
name|NetworkConnector
name|networkConnector
init|=
operator|(
name|NetworkConnector
operator|)
name|o
decl_stmt|;
if|if
condition|(
name|networkConnector
operator|.
name|getUri
argument_list|()
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|network
operator|.
name|NetworkConnector
name|nc
init|=
name|getBrokerService
argument_list|()
operator|.
name|addNetworkConnector
argument_list|(
name|networkConnector
operator|.
name|getUri
argument_list|()
argument_list|)
decl_stmt|;
name|Properties
name|properties
init|=
operator|new
name|Properties
argument_list|()
decl_stmt|;
name|IntrospectionSupport
operator|.
name|getProperties
argument_list|(
name|networkConnector
argument_list|,
name|properties
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|properties
operator|.
name|remove
argument_list|(
literal|"uri"
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|trace
argument_list|(
literal|"Applying props: "
operator|+
name|properties
argument_list|)
expr_stmt|;
name|IntrospectionSupport
operator|.
name|setProperties
argument_list|(
name|nc
argument_list|,
name|properties
argument_list|)
expr_stmt|;
name|nc
operator|.
name|start
argument_list|()
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"started new network connector: "
operator|+
name|nc
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Failed to add new networkConnector "
operator|+
name|networkConnector
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
else|else
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"No runtime support for modifications to "
operator|+
name|o
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
parameter_list|<
name|T
parameter_list|>
name|List
argument_list|<
name|T
argument_list|>
name|filterElement
parameter_list|(
name|List
argument_list|<
name|Object
argument_list|>
name|objectList
parameter_list|,
name|Class
argument_list|<
name|T
argument_list|>
name|type
parameter_list|)
block|{
name|List
argument_list|<
name|T
argument_list|>
name|result
init|=
operator|new
name|LinkedList
argument_list|<
name|T
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|Object
name|o
range|:
name|objectList
control|)
block|{
if|if
condition|(
name|o
operator|instanceof
name|JAXBElement
condition|)
block|{
name|JAXBElement
name|element
init|=
operator|(
name|JAXBElement
operator|)
name|o
decl_stmt|;
if|if
condition|(
name|element
operator|.
name|getDeclaredType
argument_list|()
operator|==
name|type
condition|)
block|{
name|result
operator|.
name|add
argument_list|(
operator|(
name|T
operator|)
name|element
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
return|return
name|result
return|;
block|}
specifier|private
name|Broker
name|loadConfiguration
parameter_list|(
name|Resource
name|configToMonitor
parameter_list|)
block|{
name|Broker
name|jaxbConfig
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|configToMonitor
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|JAXBContext
name|context
init|=
name|JAXBContext
operator|.
name|newInstance
argument_list|(
name|Broker
operator|.
name|class
argument_list|)
decl_stmt|;
name|Unmarshaller
name|unMarshaller
init|=
name|context
operator|.
name|createUnmarshaller
argument_list|()
decl_stmt|;
comment|// skip beans and pull out the broker node to validate
name|DocumentBuilderFactory
name|dbf
init|=
name|DocumentBuilderFactory
operator|.
name|newInstance
argument_list|()
decl_stmt|;
name|dbf
operator|.
name|setNamespaceAware
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|DocumentBuilder
name|db
init|=
name|dbf
operator|.
name|newDocumentBuilder
argument_list|()
decl_stmt|;
name|Document
name|doc
init|=
name|db
operator|.
name|parse
argument_list|(
name|configToMonitor
operator|.
name|getInputStream
argument_list|()
argument_list|)
decl_stmt|;
name|Node
name|brokerRootNode
init|=
name|doc
operator|.
name|getElementsByTagName
argument_list|(
literal|"broker"
argument_list|)
operator|.
name|item
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|JAXBElement
argument_list|<
name|Broker
argument_list|>
name|brokerJAXBElement
init|=
name|unMarshaller
operator|.
name|unmarshal
argument_list|(
name|brokerRootNode
argument_list|,
name|Broker
operator|.
name|class
argument_list|)
decl_stmt|;
name|jaxbConfig
operator|=
name|brokerJAXBElement
operator|.
name|getValue
argument_list|()
expr_stmt|;
comment|// if we can parse we can track mods
name|lastModified
operator|=
name|configToMonitor
operator|.
name|lastModified
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Failed to access: "
operator|+
name|configToMonitor
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|JAXBException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Failed to parse: "
operator|+
name|configToMonitor
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ParserConfigurationException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Failed to document parse: "
operator|+
name|configToMonitor
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SAXException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Failed to find broker element in: "
operator|+
name|configToMonitor
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|jaxbConfig
return|;
block|}
specifier|public
name|void
name|setCheckPeriod
parameter_list|(
name|long
name|checkPeriod
parameter_list|)
block|{
name|this
operator|.
name|checkPeriod
operator|=
name|checkPeriod
expr_stmt|;
block|}
block|}
end_class

end_unit

