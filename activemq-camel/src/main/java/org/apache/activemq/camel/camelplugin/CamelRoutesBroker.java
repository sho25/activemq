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
name|camel
operator|.
name|camelplugin
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
name|broker
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
name|broker
operator|.
name|BrokerContext
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
name|ConsumerBrokerExchange
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
name|ProducerBrokerExchange
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
name|Destination
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
name|MessageReference
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
name|Subscription
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
name|ConsumerControl
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
name|Message
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
name|MessageAck
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
name|MessageDispatch
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
name|MessagePull
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
name|Response
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
name|TransactionId
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
name|usage
operator|.
name|Usage
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
name|impl
operator|.
name|DefaultCamelContext
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
name|model
operator|.
name|RouteDefinition
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
name|model
operator|.
name|RoutesDefinition
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
name|InputStream
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
name|concurrent
operator|.
name|CountDownLatch
import|;
end_import

begin_comment
comment|/**  * A StatisticsBroker You can retrieve a Map Message for a Destination - or  * Broker containing statistics as key-value pairs The message must contain a  * replyTo Destination - else its ignored  *  */
end_comment

begin_class
specifier|public
class|class
name|CamelRoutesBroker
extends|extends
name|BrokerFilter
block|{
specifier|private
specifier|static
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|CamelRoutesBroker
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
name|String
name|routesFile
init|=
literal|""
decl_stmt|;
specifier|private
name|int
name|checkPeriod
init|=
literal|1000
decl_stmt|;
specifier|private
name|Resource
name|theRoutes
decl_stmt|;
specifier|private
name|DefaultCamelContext
name|camelContext
decl_stmt|;
specifier|private
name|long
name|lastRoutesModified
init|=
operator|-
literal|1
decl_stmt|;
specifier|private
name|CountDownLatch
name|countDownLatch
decl_stmt|;
comment|/**      * Overide methods to pause the broker whilst camel routes are loaded      */
annotation|@
name|Override
specifier|public
name|void
name|send
parameter_list|(
name|ProducerBrokerExchange
name|producerExchange
parameter_list|,
name|Message
name|message
parameter_list|)
throws|throws
name|Exception
block|{
name|blockWhileLoadingCamelRoutes
argument_list|()
expr_stmt|;
name|super
operator|.
name|send
argument_list|(
name|producerExchange
argument_list|,
name|message
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|acknowledge
parameter_list|(
name|ConsumerBrokerExchange
name|consumerExchange
parameter_list|,
name|MessageAck
name|ack
parameter_list|)
throws|throws
name|Exception
block|{
name|blockWhileLoadingCamelRoutes
argument_list|()
expr_stmt|;
name|super
operator|.
name|acknowledge
argument_list|(
name|consumerExchange
argument_list|,
name|ack
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|Response
name|messagePull
parameter_list|(
name|ConnectionContext
name|context
parameter_list|,
name|MessagePull
name|pull
parameter_list|)
throws|throws
name|Exception
block|{
name|blockWhileLoadingCamelRoutes
argument_list|()
expr_stmt|;
return|return
name|super
operator|.
name|messagePull
argument_list|(
name|context
argument_list|,
name|pull
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|processConsumerControl
parameter_list|(
name|ConsumerBrokerExchange
name|consumerExchange
parameter_list|,
name|ConsumerControl
name|control
parameter_list|)
block|{
name|blockWhileLoadingCamelRoutes
argument_list|()
expr_stmt|;
name|super
operator|.
name|processConsumerControl
argument_list|(
name|consumerExchange
argument_list|,
name|control
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|reapplyInterceptor
parameter_list|()
block|{
name|blockWhileLoadingCamelRoutes
argument_list|()
expr_stmt|;
name|super
operator|.
name|reapplyInterceptor
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|beginTransaction
parameter_list|(
name|ConnectionContext
name|context
parameter_list|,
name|TransactionId
name|xid
parameter_list|)
throws|throws
name|Exception
block|{
name|blockWhileLoadingCamelRoutes
argument_list|()
expr_stmt|;
name|super
operator|.
name|beginTransaction
argument_list|(
name|context
argument_list|,
name|xid
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|prepareTransaction
parameter_list|(
name|ConnectionContext
name|context
parameter_list|,
name|TransactionId
name|xid
parameter_list|)
throws|throws
name|Exception
block|{
name|blockWhileLoadingCamelRoutes
argument_list|()
expr_stmt|;
return|return
name|super
operator|.
name|prepareTransaction
argument_list|(
name|context
argument_list|,
name|xid
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|rollbackTransaction
parameter_list|(
name|ConnectionContext
name|context
parameter_list|,
name|TransactionId
name|xid
parameter_list|)
throws|throws
name|Exception
block|{
name|blockWhileLoadingCamelRoutes
argument_list|()
expr_stmt|;
name|super
operator|.
name|rollbackTransaction
argument_list|(
name|context
argument_list|,
name|xid
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|commitTransaction
parameter_list|(
name|ConnectionContext
name|context
parameter_list|,
name|TransactionId
name|xid
parameter_list|,
name|boolean
name|onePhase
parameter_list|)
throws|throws
name|Exception
block|{
name|blockWhileLoadingCamelRoutes
argument_list|()
expr_stmt|;
name|super
operator|.
name|commitTransaction
argument_list|(
name|context
argument_list|,
name|xid
argument_list|,
name|onePhase
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|forgetTransaction
parameter_list|(
name|ConnectionContext
name|context
parameter_list|,
name|TransactionId
name|transactionId
parameter_list|)
throws|throws
name|Exception
block|{
name|blockWhileLoadingCamelRoutes
argument_list|()
expr_stmt|;
name|super
operator|.
name|forgetTransaction
argument_list|(
name|context
argument_list|,
name|transactionId
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|preProcessDispatch
parameter_list|(
name|MessageDispatch
name|messageDispatch
parameter_list|)
block|{
name|blockWhileLoadingCamelRoutes
argument_list|()
expr_stmt|;
name|super
operator|.
name|preProcessDispatch
argument_list|(
name|messageDispatch
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|postProcessDispatch
parameter_list|(
name|MessageDispatch
name|messageDispatch
parameter_list|)
block|{
name|blockWhileLoadingCamelRoutes
argument_list|()
expr_stmt|;
name|super
operator|.
name|postProcessDispatch
argument_list|(
name|messageDispatch
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|sendToDeadLetterQueue
parameter_list|(
name|ConnectionContext
name|context
parameter_list|,
name|MessageReference
name|messageReference
parameter_list|,
name|Subscription
name|subscription
parameter_list|,
name|Throwable
name|poisonCause
parameter_list|)
block|{
name|blockWhileLoadingCamelRoutes
argument_list|()
expr_stmt|;
return|return
name|super
operator|.
name|sendToDeadLetterQueue
argument_list|(
name|context
argument_list|,
name|messageReference
argument_list|,
name|subscription
argument_list|,
name|poisonCause
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|messageConsumed
parameter_list|(
name|ConnectionContext
name|context
parameter_list|,
name|MessageReference
name|messageReference
parameter_list|)
block|{
name|blockWhileLoadingCamelRoutes
argument_list|()
expr_stmt|;
name|super
operator|.
name|messageConsumed
argument_list|(
name|context
argument_list|,
name|messageReference
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|messageDelivered
parameter_list|(
name|ConnectionContext
name|context
parameter_list|,
name|MessageReference
name|messageReference
parameter_list|)
block|{
name|blockWhileLoadingCamelRoutes
argument_list|()
expr_stmt|;
name|super
operator|.
name|messageDelivered
argument_list|(
name|context
argument_list|,
name|messageReference
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|messageDiscarded
parameter_list|(
name|ConnectionContext
name|context
parameter_list|,
name|Subscription
name|sub
parameter_list|,
name|MessageReference
name|messageReference
parameter_list|)
block|{
name|blockWhileLoadingCamelRoutes
argument_list|()
expr_stmt|;
name|super
operator|.
name|messageDiscarded
argument_list|(
name|context
argument_list|,
name|sub
argument_list|,
name|messageReference
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|isFull
parameter_list|(
name|ConnectionContext
name|context
parameter_list|,
name|Destination
name|destination
parameter_list|,
name|Usage
argument_list|<
name|?
argument_list|>
name|usage
parameter_list|)
block|{
name|blockWhileLoadingCamelRoutes
argument_list|()
expr_stmt|;
name|super
operator|.
name|isFull
argument_list|(
name|context
argument_list|,
name|destination
argument_list|,
name|usage
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|nowMasterBroker
parameter_list|()
block|{
name|blockWhileLoadingCamelRoutes
argument_list|()
expr_stmt|;
name|super
operator|.
name|nowMasterBroker
argument_list|()
expr_stmt|;
block|}
comment|/*      * Properties      */
specifier|public
name|String
name|getRoutesFile
parameter_list|()
block|{
return|return
name|routesFile
return|;
block|}
specifier|public
name|void
name|setRoutesFile
parameter_list|(
name|String
name|routesFile
parameter_list|)
block|{
name|this
operator|.
name|routesFile
operator|=
name|routesFile
expr_stmt|;
block|}
specifier|public
name|int
name|getCheckPeriod
parameter_list|()
block|{
return|return
name|checkPeriod
return|;
block|}
specifier|public
name|void
name|setCheckPeriod
parameter_list|(
name|int
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
specifier|public
name|CamelRoutesBroker
parameter_list|(
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
name|LOG
operator|.
name|info
argument_list|(
literal|"Starting CamelRoutesBroker"
argument_list|)
expr_stmt|;
name|camelContext
operator|=
operator|new
name|DefaultCamelContext
argument_list|()
expr_stmt|;
name|camelContext
operator|.
name|setName
argument_list|(
literal|"EmbeddedCamel-"
operator|+
name|getBrokerName
argument_list|()
argument_list|)
expr_stmt|;
name|camelContext
operator|.
name|start
argument_list|()
expr_stmt|;
name|getBrokerService
argument_list|()
operator|.
name|getScheduler
argument_list|()
operator|.
name|executePeriodically
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
name|loadCamelRoutes
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Failed to load Camel Routes"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
argument_list|,
name|getCheckPeriod
argument_list|()
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
name|CountDownLatch
name|latch
init|=
name|this
operator|.
name|countDownLatch
decl_stmt|;
if|if
condition|(
name|latch
operator|!=
literal|null
condition|)
block|{
name|latch
operator|.
name|countDown
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|camelContext
operator|!=
literal|null
condition|)
block|{
name|camelContext
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
name|super
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
specifier|private
name|void
name|loadCamelRoutes
parameter_list|()
throws|throws
name|Exception
block|{
if|if
condition|(
name|theRoutes
operator|==
literal|null
condition|)
block|{
name|String
name|fileToUse
init|=
name|getRoutesFile
argument_list|()
decl_stmt|;
if|if
condition|(
name|fileToUse
operator|==
literal|null
operator|||
name|fileToUse
operator|.
name|trim
argument_list|()
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|BrokerContext
name|brokerContext
init|=
name|getBrokerService
argument_list|()
operator|.
name|getBrokerContext
argument_list|()
decl_stmt|;
if|if
condition|(
name|brokerContext
operator|!=
literal|null
condition|)
block|{
name|String
name|uri
init|=
name|brokerContext
operator|.
name|getConfigurationUrl
argument_list|()
decl_stmt|;
name|Resource
name|resource
init|=
name|Utils
operator|.
name|resourceFromString
argument_list|(
name|uri
argument_list|)
decl_stmt|;
if|if
condition|(
name|resource
operator|.
name|exists
argument_list|()
condition|)
block|{
name|fileToUse
operator|=
name|resource
operator|.
name|getFile
argument_list|()
operator|.
name|getParent
argument_list|()
expr_stmt|;
name|fileToUse
operator|+=
name|File
operator|.
name|separator
expr_stmt|;
name|fileToUse
operator|+=
literal|"routes.xml"
expr_stmt|;
block|}
block|}
block|}
if|if
condition|(
name|fileToUse
operator|!=
literal|null
operator|&&
operator|!
name|fileToUse
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|theRoutes
operator|=
name|Utils
operator|.
name|resourceFromString
argument_list|(
name|fileToUse
argument_list|)
expr_stmt|;
name|setRoutesFile
argument_list|(
name|theRoutes
operator|.
name|getFile
argument_list|()
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
operator|!
name|isStopped
argument_list|()
operator|&&
name|camelContext
operator|!=
literal|null
operator|&&
name|theRoutes
operator|!=
literal|null
operator|&&
name|theRoutes
operator|.
name|exists
argument_list|()
condition|)
block|{
name|long
name|lastModified
init|=
name|theRoutes
operator|.
name|lastModified
argument_list|()
decl_stmt|;
if|if
condition|(
name|lastModified
operator|!=
name|lastRoutesModified
condition|)
block|{
name|CountDownLatch
name|latch
init|=
operator|new
name|CountDownLatch
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|this
operator|.
name|countDownLatch
operator|=
name|latch
expr_stmt|;
name|lastRoutesModified
operator|=
name|lastModified
expr_stmt|;
name|List
argument_list|<
name|RouteDefinition
argument_list|>
name|currentRoutes
init|=
name|camelContext
operator|.
name|getRouteDefinitions
argument_list|()
decl_stmt|;
for|for
control|(
name|RouteDefinition
name|rd
range|:
name|currentRoutes
control|)
block|{
name|camelContext
operator|.
name|stopRoute
argument_list|(
name|rd
argument_list|)
expr_stmt|;
name|camelContext
operator|.
name|removeRouteDefinition
argument_list|(
name|rd
argument_list|)
expr_stmt|;
block|}
name|InputStream
name|is
init|=
name|theRoutes
operator|.
name|getInputStream
argument_list|()
decl_stmt|;
name|RoutesDefinition
name|routesDefinition
init|=
name|camelContext
operator|.
name|loadRoutesDefinition
argument_list|(
name|is
argument_list|)
decl_stmt|;
for|for
control|(
name|RouteDefinition
name|rd
range|:
name|routesDefinition
operator|.
name|getRoutes
argument_list|()
control|)
block|{
name|camelContext
operator|.
name|startRoute
argument_list|(
name|rd
argument_list|)
expr_stmt|;
block|}
name|is
operator|.
name|close
argument_list|()
expr_stmt|;
name|latch
operator|.
name|countDown
argument_list|()
expr_stmt|;
name|this
operator|.
name|countDownLatch
operator|=
literal|null
expr_stmt|;
block|}
block|}
block|}
specifier|private
name|void
name|blockWhileLoadingCamelRoutes
parameter_list|()
block|{
name|CountDownLatch
name|latch
init|=
name|this
operator|.
name|countDownLatch
decl_stmt|;
if|if
condition|(
name|latch
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|latch
operator|.
name|await
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|interrupt
argument_list|()
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

