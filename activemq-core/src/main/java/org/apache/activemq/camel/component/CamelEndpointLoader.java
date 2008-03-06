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
name|camel
operator|.
name|component
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
name|javax
operator|.
name|jms
operator|.
name|Connection
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
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|ActiveMQConnection
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
name|advisory
operator|.
name|DestinationEvent
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
name|advisory
operator|.
name|DestinationListener
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
name|advisory
operator|.
name|DestinationSource
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
name|ActiveMQDestination
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
name|ActiveMQQueue
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|camel
operator|.
name|CamelContext
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|camel
operator|.
name|CamelContextAware
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|camel
operator|.
name|Endpoint
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|camel
operator|.
name|component
operator|.
name|jms
operator|.
name|JmsEndpoint
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|camel
operator|.
name|util
operator|.
name|ObjectHelper
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
name|springframework
operator|.
name|beans
operator|.
name|factory
operator|.
name|InitializingBean
import|;
end_import

begin_comment
comment|/**  * A helper bean which populates a {@link CamelContext} with ActiveMQ Queue endpoints  *   * @version $Revision: 1.1 $  */
end_comment

begin_class
specifier|public
class|class
name|CamelEndpointLoader
implements|implements
name|InitializingBean
implements|,
name|CamelContextAware
block|{
specifier|private
specifier|static
specifier|final
specifier|transient
name|Log
name|LOG
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|CamelEndpointLoader
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
name|CamelContext
name|camelContext
decl_stmt|;
specifier|private
name|ActiveMQConnection
name|connection
decl_stmt|;
specifier|private
name|ConnectionFactory
name|connectionFactory
decl_stmt|;
specifier|private
name|ActiveMQComponent
name|component
decl_stmt|;
specifier|public
name|CamelEndpointLoader
parameter_list|()
block|{     }
specifier|public
name|CamelEndpointLoader
parameter_list|(
name|CamelContext
name|camelContext
parameter_list|)
block|{
name|this
operator|.
name|camelContext
operator|=
name|camelContext
expr_stmt|;
block|}
specifier|public
name|CamelContext
name|getCamelContext
parameter_list|()
block|{
return|return
name|camelContext
return|;
block|}
specifier|public
name|void
name|setCamelContext
parameter_list|(
name|CamelContext
name|camelContext
parameter_list|)
block|{
name|this
operator|.
name|camelContext
operator|=
name|camelContext
expr_stmt|;
block|}
specifier|public
name|ActiveMQConnection
name|getConnection
parameter_list|()
block|{
return|return
name|connection
return|;
block|}
specifier|public
name|ConnectionFactory
name|getConnectionFactory
parameter_list|()
block|{
if|if
condition|(
name|connectionFactory
operator|==
literal|null
condition|)
block|{
name|connectionFactory
operator|=
name|getComponent
argument_list|()
operator|.
name|getConfiguration
argument_list|()
operator|.
name|createConnectionFactory
argument_list|()
expr_stmt|;
block|}
return|return
name|connectionFactory
return|;
block|}
specifier|public
name|void
name|setConnectionFactory
parameter_list|(
name|ConnectionFactory
name|connectionFactory
parameter_list|)
block|{
name|this
operator|.
name|connectionFactory
operator|=
name|connectionFactory
expr_stmt|;
block|}
specifier|public
name|ActiveMQComponent
name|getComponent
parameter_list|()
block|{
if|if
condition|(
name|component
operator|==
literal|null
condition|)
block|{
name|component
operator|=
name|camelContext
operator|.
name|getComponent
argument_list|(
literal|"activemq"
argument_list|,
name|ActiveMQComponent
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
return|return
name|component
return|;
block|}
specifier|public
name|void
name|setComponent
parameter_list|(
name|ActiveMQComponent
name|component
parameter_list|)
block|{
name|this
operator|.
name|component
operator|=
name|component
expr_stmt|;
block|}
specifier|public
name|void
name|afterPropertiesSet
parameter_list|()
throws|throws
name|Exception
block|{
name|ObjectHelper
operator|.
name|notNull
argument_list|(
name|camelContext
argument_list|,
literal|"camelContext"
argument_list|)
expr_stmt|;
if|if
condition|(
name|connection
operator|==
literal|null
condition|)
block|{
name|Connection
name|value
init|=
name|getConnectionFactory
argument_list|()
operator|.
name|createConnection
argument_list|()
decl_stmt|;
if|if
condition|(
name|value
operator|instanceof
name|ActiveMQConnection
condition|)
block|{
name|connection
operator|=
operator|(
name|ActiveMQConnection
operator|)
name|value
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Created JMS Connection is not an ActiveMQConnection: "
operator|+
name|value
argument_list|)
throw|;
block|}
block|}
name|DestinationSource
name|source
init|=
name|connection
operator|.
name|getDestinationSource
argument_list|()
decl_stmt|;
name|source
operator|.
name|setDestinationListener
argument_list|(
operator|new
name|DestinationListener
argument_list|()
block|{
specifier|public
name|void
name|onDestinationEvent
parameter_list|(
name|DestinationEvent
name|event
parameter_list|)
block|{
try|try
block|{
name|ActiveMQDestination
name|destination
init|=
name|event
operator|.
name|getDestination
argument_list|()
decl_stmt|;
if|if
condition|(
name|destination
operator|instanceof
name|ActiveMQQueue
condition|)
block|{
name|ActiveMQQueue
name|queue
init|=
operator|(
name|ActiveMQQueue
operator|)
name|destination
decl_stmt|;
if|if
condition|(
name|event
operator|.
name|isAddOperation
argument_list|()
condition|)
block|{
name|addQueue
argument_list|(
name|queue
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|removeQueue
argument_list|(
name|queue
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
literal|"Caught: "
operator|+
name|e
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
argument_list|)
expr_stmt|;
name|Set
argument_list|<
name|ActiveMQQueue
argument_list|>
name|queues
init|=
name|source
operator|.
name|getQueues
argument_list|()
decl_stmt|;
for|for
control|(
name|ActiveMQQueue
name|queue
range|:
name|queues
control|)
block|{
name|addQueue
argument_list|(
name|queue
argument_list|)
expr_stmt|;
block|}
block|}
specifier|protected
name|void
name|addQueue
parameter_list|(
name|ActiveMQQueue
name|queue
parameter_list|)
throws|throws
name|Exception
block|{
name|String
name|queueUri
init|=
name|getQueueUri
argument_list|(
name|queue
argument_list|)
decl_stmt|;
name|ActiveMQComponent
name|jmsComponent
init|=
name|getComponent
argument_list|()
decl_stmt|;
name|Endpoint
name|endpoint
init|=
operator|new
name|JmsEndpoint
argument_list|(
name|queueUri
argument_list|,
name|jmsComponent
argument_list|,
name|queue
operator|.
name|getPhysicalName
argument_list|()
argument_list|,
literal|false
argument_list|,
name|jmsComponent
operator|.
name|getConfiguration
argument_list|()
argument_list|)
decl_stmt|;
name|camelContext
operator|.
name|addSingletonEndpoint
argument_list|(
name|queueUri
argument_list|,
name|endpoint
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|String
name|getQueueUri
parameter_list|(
name|ActiveMQQueue
name|queue
parameter_list|)
block|{
return|return
literal|"activemq:"
operator|+
name|queue
operator|.
name|getPhysicalName
argument_list|()
return|;
block|}
specifier|protected
name|void
name|removeQueue
parameter_list|(
name|ActiveMQQueue
name|queue
parameter_list|)
throws|throws
name|Exception
block|{
name|String
name|queueUri
init|=
name|getQueueUri
argument_list|(
name|queue
argument_list|)
decl_stmt|;
name|camelContext
operator|.
name|removeSingletonEndpoint
argument_list|(
name|queueUri
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

