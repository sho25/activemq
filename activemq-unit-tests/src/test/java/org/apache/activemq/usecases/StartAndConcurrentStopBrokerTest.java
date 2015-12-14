begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *<p/>  * http://www.apache.org/licenses/LICENSE-2.0  *<p/>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
package|package
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|usecases
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|ObjectInputStream
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
name|Set
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|CountDownLatch
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|ExecutorService
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|Executors
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|TimeUnit
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|atomic
operator|.
name|AtomicBoolean
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|atomic
operator|.
name|AtomicReference
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|management
operator|.
name|Attribute
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|management
operator|.
name|AttributeList
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|management
operator|.
name|AttributeNotFoundException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|management
operator|.
name|InstanceAlreadyExistsException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|management
operator|.
name|InstanceNotFoundException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|management
operator|.
name|IntrospectionException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|management
operator|.
name|InvalidAttributeValueException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|management
operator|.
name|ListenerNotFoundException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|management
operator|.
name|MBeanException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|management
operator|.
name|MBeanInfo
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|management
operator|.
name|MBeanRegistrationException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|management
operator|.
name|MBeanServer
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|management
operator|.
name|NotCompliantMBeanException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|management
operator|.
name|NotificationFilter
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|management
operator|.
name|NotificationListener
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|management
operator|.
name|ObjectInstance
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|management
operator|.
name|ObjectName
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|management
operator|.
name|OperationsException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|management
operator|.
name|QueryExp
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|management
operator|.
name|ReflectionException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|management
operator|.
name|loading
operator|.
name|ClassLoaderRepository
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
name|ConfigurationException
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
name|broker
operator|.
name|BrokerStoppedException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Test
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
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertNull
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertTrue
import|;
end_import

begin_class
specifier|public
class|class
name|StartAndConcurrentStopBrokerTest
block|{
specifier|private
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|StartAndConcurrentStopBrokerTest
operator|.
name|class
argument_list|)
decl_stmt|;
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|30000
argument_list|)
specifier|public
name|void
name|testConcurrentStop
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|AtomicReference
argument_list|<
name|Throwable
argument_list|>
name|error
init|=
operator|new
name|AtomicReference
argument_list|<
name|Throwable
argument_list|>
argument_list|()
decl_stmt|;
specifier|final
name|CountDownLatch
name|gotBrokerMbean
init|=
operator|new
name|CountDownLatch
argument_list|(
literal|1
argument_list|)
decl_stmt|;
specifier|final
name|CountDownLatch
name|gotPaMBean
init|=
operator|new
name|CountDownLatch
argument_list|(
literal|1
argument_list|)
decl_stmt|;
specifier|final
name|AtomicBoolean
name|checkPaMBean
init|=
operator|new
name|AtomicBoolean
argument_list|(
literal|false
argument_list|)
decl_stmt|;
specifier|final
name|HashMap
name|mbeans
init|=
operator|new
name|HashMap
argument_list|()
decl_stmt|;
specifier|final
name|MBeanServer
name|mBeanServer
init|=
operator|new
name|MBeanServer
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|ObjectInstance
name|createMBean
parameter_list|(
name|String
name|className
parameter_list|,
name|ObjectName
name|name
parameter_list|)
throws|throws
name|ReflectionException
throws|,
name|InstanceAlreadyExistsException
throws|,
name|MBeanRegistrationException
throws|,
name|MBeanException
throws|,
name|NotCompliantMBeanException
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
specifier|public
name|ObjectInstance
name|createMBean
parameter_list|(
name|String
name|className
parameter_list|,
name|ObjectName
name|name
parameter_list|,
name|ObjectName
name|loaderName
parameter_list|)
throws|throws
name|ReflectionException
throws|,
name|InstanceAlreadyExistsException
throws|,
name|MBeanRegistrationException
throws|,
name|MBeanException
throws|,
name|NotCompliantMBeanException
throws|,
name|InstanceNotFoundException
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
specifier|public
name|ObjectInstance
name|createMBean
parameter_list|(
name|String
name|className
parameter_list|,
name|ObjectName
name|name
parameter_list|,
name|Object
index|[]
name|params
parameter_list|,
name|String
index|[]
name|signature
parameter_list|)
throws|throws
name|ReflectionException
throws|,
name|InstanceAlreadyExistsException
throws|,
name|MBeanRegistrationException
throws|,
name|MBeanException
throws|,
name|NotCompliantMBeanException
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
specifier|public
name|ObjectInstance
name|createMBean
parameter_list|(
name|String
name|className
parameter_list|,
name|ObjectName
name|name
parameter_list|,
name|ObjectName
name|loaderName
parameter_list|,
name|Object
index|[]
name|params
parameter_list|,
name|String
index|[]
name|signature
parameter_list|)
throws|throws
name|ReflectionException
throws|,
name|InstanceAlreadyExistsException
throws|,
name|MBeanRegistrationException
throws|,
name|MBeanException
throws|,
name|NotCompliantMBeanException
throws|,
name|InstanceNotFoundException
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
specifier|public
name|ObjectInstance
name|registerMBean
parameter_list|(
name|Object
name|object
parameter_list|,
name|ObjectName
name|name
parameter_list|)
throws|throws
name|InstanceAlreadyExistsException
throws|,
name|MBeanRegistrationException
throws|,
name|NotCompliantMBeanException
block|{
if|if
condition|(
name|mbeans
operator|.
name|containsKey
argument_list|(
name|name
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|InstanceAlreadyExistsException
argument_list|(
literal|"Got one already: "
operator|+
name|name
argument_list|)
throw|;
block|}
name|LOG
operator|.
name|info
argument_list|(
literal|"register:"
operator|+
name|name
argument_list|)
expr_stmt|;
try|try
block|{
if|if
condition|(
name|name
operator|.
name|compareTo
argument_list|(
operator|new
name|ObjectName
argument_list|(
literal|"org.apache.activemq:type=Broker,brokerName=localhost"
argument_list|)
argument_list|)
operator|==
literal|0
condition|)
block|{
name|gotBrokerMbean
operator|.
name|countDown
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|checkPaMBean
operator|.
name|get
argument_list|()
condition|)
block|{
if|if
condition|(
operator|new
name|ObjectName
argument_list|(
literal|"org.apache.activemq:type=Broker,brokerName=localhost,service=PersistenceAdapter,instanceName=*"
argument_list|)
operator|.
name|apply
argument_list|(
name|name
argument_list|)
condition|)
block|{
name|gotPaMBean
operator|.
name|countDown
argument_list|()
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
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
name|error
operator|.
name|set
argument_list|(
name|e
argument_list|)
expr_stmt|;
block|}
name|mbeans
operator|.
name|put
argument_list|(
name|name
argument_list|,
name|object
argument_list|)
expr_stmt|;
return|return
operator|new
name|ObjectInstance
argument_list|(
name|name
argument_list|,
name|object
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|unregisterMBean
parameter_list|(
name|ObjectName
name|name
parameter_list|)
throws|throws
name|InstanceNotFoundException
throws|,
name|MBeanRegistrationException
block|{
name|mbeans
operator|.
name|remove
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|ObjectInstance
name|getObjectInstance
parameter_list|(
name|ObjectName
name|name
parameter_list|)
throws|throws
name|InstanceNotFoundException
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
specifier|public
name|Set
argument_list|<
name|ObjectInstance
argument_list|>
name|queryMBeans
parameter_list|(
name|ObjectName
name|name
parameter_list|,
name|QueryExp
name|query
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
specifier|public
name|Set
argument_list|<
name|ObjectName
argument_list|>
name|queryNames
parameter_list|(
name|ObjectName
name|name
parameter_list|,
name|QueryExp
name|query
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isRegistered
parameter_list|(
name|ObjectName
name|name
parameter_list|)
block|{
return|return
name|mbeans
operator|.
name|containsKey
argument_list|(
name|name
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|Integer
name|getMBeanCount
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
specifier|public
name|Object
name|getAttribute
parameter_list|(
name|ObjectName
name|name
parameter_list|,
name|String
name|attribute
parameter_list|)
throws|throws
name|MBeanException
throws|,
name|AttributeNotFoundException
throws|,
name|InstanceNotFoundException
throws|,
name|ReflectionException
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
specifier|public
name|AttributeList
name|getAttributes
parameter_list|(
name|ObjectName
name|name
parameter_list|,
name|String
index|[]
name|attributes
parameter_list|)
throws|throws
name|InstanceNotFoundException
throws|,
name|ReflectionException
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|setAttribute
parameter_list|(
name|ObjectName
name|name
parameter_list|,
name|Attribute
name|attribute
parameter_list|)
throws|throws
name|InstanceNotFoundException
throws|,
name|AttributeNotFoundException
throws|,
name|InvalidAttributeValueException
throws|,
name|MBeanException
throws|,
name|ReflectionException
block|{              }
annotation|@
name|Override
specifier|public
name|AttributeList
name|setAttributes
parameter_list|(
name|ObjectName
name|name
parameter_list|,
name|AttributeList
name|attributes
parameter_list|)
throws|throws
name|InstanceNotFoundException
throws|,
name|ReflectionException
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
specifier|public
name|Object
name|invoke
parameter_list|(
name|ObjectName
name|name
parameter_list|,
name|String
name|operationName
parameter_list|,
name|Object
index|[]
name|params
parameter_list|,
name|String
index|[]
name|signature
parameter_list|)
throws|throws
name|InstanceNotFoundException
throws|,
name|MBeanException
throws|,
name|ReflectionException
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getDefaultDomain
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
index|[]
name|getDomains
parameter_list|()
block|{
return|return
operator|new
name|String
index|[
literal|0
index|]
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|addNotificationListener
parameter_list|(
name|ObjectName
name|name
parameter_list|,
name|NotificationListener
name|listener
parameter_list|,
name|NotificationFilter
name|filter
parameter_list|,
name|Object
name|handback
parameter_list|)
throws|throws
name|InstanceNotFoundException
block|{              }
annotation|@
name|Override
specifier|public
name|void
name|addNotificationListener
parameter_list|(
name|ObjectName
name|name
parameter_list|,
name|ObjectName
name|listener
parameter_list|,
name|NotificationFilter
name|filter
parameter_list|,
name|Object
name|handback
parameter_list|)
throws|throws
name|InstanceNotFoundException
block|{              }
annotation|@
name|Override
specifier|public
name|void
name|removeNotificationListener
parameter_list|(
name|ObjectName
name|name
parameter_list|,
name|ObjectName
name|listener
parameter_list|)
throws|throws
name|InstanceNotFoundException
throws|,
name|ListenerNotFoundException
block|{              }
annotation|@
name|Override
specifier|public
name|void
name|removeNotificationListener
parameter_list|(
name|ObjectName
name|name
parameter_list|,
name|ObjectName
name|listener
parameter_list|,
name|NotificationFilter
name|filter
parameter_list|,
name|Object
name|handback
parameter_list|)
throws|throws
name|InstanceNotFoundException
throws|,
name|ListenerNotFoundException
block|{              }
annotation|@
name|Override
specifier|public
name|void
name|removeNotificationListener
parameter_list|(
name|ObjectName
name|name
parameter_list|,
name|NotificationListener
name|listener
parameter_list|)
throws|throws
name|InstanceNotFoundException
throws|,
name|ListenerNotFoundException
block|{              }
annotation|@
name|Override
specifier|public
name|void
name|removeNotificationListener
parameter_list|(
name|ObjectName
name|name
parameter_list|,
name|NotificationListener
name|listener
parameter_list|,
name|NotificationFilter
name|filter
parameter_list|,
name|Object
name|handback
parameter_list|)
throws|throws
name|InstanceNotFoundException
throws|,
name|ListenerNotFoundException
block|{              }
annotation|@
name|Override
specifier|public
name|MBeanInfo
name|getMBeanInfo
parameter_list|(
name|ObjectName
name|name
parameter_list|)
throws|throws
name|InstanceNotFoundException
throws|,
name|IntrospectionException
throws|,
name|ReflectionException
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isInstanceOf
parameter_list|(
name|ObjectName
name|name
parameter_list|,
name|String
name|className
parameter_list|)
throws|throws
name|InstanceNotFoundException
block|{
return|return
literal|false
return|;
block|}
annotation|@
name|Override
specifier|public
name|Object
name|instantiate
parameter_list|(
name|String
name|className
parameter_list|)
throws|throws
name|ReflectionException
throws|,
name|MBeanException
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
specifier|public
name|Object
name|instantiate
parameter_list|(
name|String
name|className
parameter_list|,
name|ObjectName
name|loaderName
parameter_list|)
throws|throws
name|ReflectionException
throws|,
name|MBeanException
throws|,
name|InstanceNotFoundException
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
specifier|public
name|Object
name|instantiate
parameter_list|(
name|String
name|className
parameter_list|,
name|Object
index|[]
name|params
parameter_list|,
name|String
index|[]
name|signature
parameter_list|)
throws|throws
name|ReflectionException
throws|,
name|MBeanException
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
specifier|public
name|Object
name|instantiate
parameter_list|(
name|String
name|className
parameter_list|,
name|ObjectName
name|loaderName
parameter_list|,
name|Object
index|[]
name|params
parameter_list|,
name|String
index|[]
name|signature
parameter_list|)
throws|throws
name|ReflectionException
throws|,
name|MBeanException
throws|,
name|InstanceNotFoundException
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
specifier|public
name|ObjectInputStream
name|deserialize
parameter_list|(
name|ObjectName
name|name
parameter_list|,
name|byte
index|[]
name|data
parameter_list|)
throws|throws
name|InstanceNotFoundException
throws|,
name|OperationsException
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
specifier|public
name|ObjectInputStream
name|deserialize
parameter_list|(
name|String
name|className
parameter_list|,
name|byte
index|[]
name|data
parameter_list|)
throws|throws
name|OperationsException
throws|,
name|ReflectionException
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
specifier|public
name|ObjectInputStream
name|deserialize
parameter_list|(
name|String
name|className
parameter_list|,
name|ObjectName
name|loaderName
parameter_list|,
name|byte
index|[]
name|data
parameter_list|)
throws|throws
name|InstanceNotFoundException
throws|,
name|OperationsException
throws|,
name|ReflectionException
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
specifier|public
name|ClassLoader
name|getClassLoaderFor
parameter_list|(
name|ObjectName
name|mbeanName
parameter_list|)
throws|throws
name|InstanceNotFoundException
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
specifier|public
name|ClassLoader
name|getClassLoader
parameter_list|(
name|ObjectName
name|loaderName
parameter_list|)
throws|throws
name|InstanceNotFoundException
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
specifier|public
name|ClassLoaderRepository
name|getClassLoaderRepository
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
block|}
decl_stmt|;
specifier|final
name|BrokerService
name|broker
init|=
operator|new
name|BrokerService
argument_list|()
decl_stmt|;
name|broker
operator|.
name|setDeleteAllMessagesOnStartup
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|ExecutorService
name|executor
init|=
name|Executors
operator|.
name|newFixedThreadPool
argument_list|(
literal|4
argument_list|)
decl_stmt|;
name|executor
operator|.
name|execute
argument_list|(
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
name|broker
operator|.
name|getManagementContext
argument_list|()
operator|.
name|setMBeanServer
argument_list|(
name|mBeanServer
argument_list|)
expr_stmt|;
name|broker
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|BrokerStoppedException
name|expected
parameter_list|)
block|{                 }
catch|catch
parameter_list|(
name|ConfigurationException
name|expected
parameter_list|)
block|{                 }
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
name|error
operator|.
name|set
argument_list|(
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
argument_list|)
expr_stmt|;
name|executor
operator|.
name|execute
argument_list|(
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
name|assertTrue
argument_list|(
literal|"broker has registered mbean"
argument_list|,
name|gotBrokerMbean
operator|.
name|await
argument_list|(
literal|10
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
argument_list|)
expr_stmt|;
name|broker
operator|.
name|stop
argument_list|()
expr_stmt|;
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
name|error
operator|.
name|set
argument_list|(
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
argument_list|)
expr_stmt|;
name|executor
operator|.
name|shutdown
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
literal|"stop tasks done"
argument_list|,
name|executor
operator|.
name|awaitTermination
argument_list|(
literal|20
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
argument_list|)
expr_stmt|;
name|BrokerService
name|sanityBroker
init|=
operator|new
name|BrokerService
argument_list|()
decl_stmt|;
name|sanityBroker
operator|.
name|getManagementContext
argument_list|()
operator|.
name|setMBeanServer
argument_list|(
name|mBeanServer
argument_list|)
expr_stmt|;
name|sanityBroker
operator|.
name|start
argument_list|()
expr_stmt|;
name|sanityBroker
operator|.
name|stop
argument_list|()
expr_stmt|;
name|assertNull
argument_list|(
literal|"No error"
argument_list|,
name|error
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
comment|// again, after Persistence adapter mbean
specifier|final
name|BrokerService
name|brokerTwo
init|=
operator|new
name|BrokerService
argument_list|()
decl_stmt|;
name|broker
operator|.
name|setDeleteAllMessagesOnStartup
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|checkPaMBean
operator|.
name|set
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|executor
operator|=
name|Executors
operator|.
name|newFixedThreadPool
argument_list|(
literal|4
argument_list|)
expr_stmt|;
name|executor
operator|.
name|execute
argument_list|(
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
name|brokerTwo
operator|.
name|getManagementContext
argument_list|()
operator|.
name|setMBeanServer
argument_list|(
name|mBeanServer
argument_list|)
expr_stmt|;
name|brokerTwo
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|BrokerStoppedException
name|expected
parameter_list|)
block|{                 }
catch|catch
parameter_list|(
name|ConfigurationException
name|expected
parameter_list|)
block|{                 }
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
name|error
operator|.
name|set
argument_list|(
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
argument_list|)
expr_stmt|;
name|executor
operator|.
name|execute
argument_list|(
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
name|assertTrue
argument_list|(
literal|"broker has registered persistence adapter mbean"
argument_list|,
name|gotPaMBean
operator|.
name|await
argument_list|(
literal|10
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
argument_list|)
expr_stmt|;
name|brokerTwo
operator|.
name|stop
argument_list|()
expr_stmt|;
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
name|error
operator|.
name|set
argument_list|(
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
argument_list|)
expr_stmt|;
name|executor
operator|.
name|shutdown
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
literal|"stop tasks done"
argument_list|,
name|executor
operator|.
name|awaitTermination
argument_list|(
literal|20
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"broker has registered persistence adapter mbean"
argument_list|,
name|gotPaMBean
operator|.
name|await
argument_list|(
literal|0
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
argument_list|)
expr_stmt|;
name|sanityBroker
operator|=
operator|new
name|BrokerService
argument_list|()
expr_stmt|;
name|sanityBroker
operator|.
name|getManagementContext
argument_list|()
operator|.
name|setMBeanServer
argument_list|(
name|mBeanServer
argument_list|)
expr_stmt|;
name|sanityBroker
operator|.
name|start
argument_list|()
expr_stmt|;
name|sanityBroker
operator|.
name|stop
argument_list|()
expr_stmt|;
name|assertNull
argument_list|(
literal|"No error"
argument_list|,
name|error
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

