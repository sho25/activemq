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
name|broker
operator|.
name|util
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
name|BrokerPluginSupport
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

begin_comment
comment|/**  * A simple Broker interceptor which allows you to enable/disable logging.  *   * @org.apache.xbean.XBean  *   * @version $Revision$  */
end_comment

begin_class
specifier|public
class|class
name|LoggingBrokerPlugin
extends|extends
name|BrokerPluginSupport
block|{
specifier|private
name|Log
name|log
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|LoggingBrokerPlugin
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
name|Log
name|sendLog
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|LoggingBrokerPlugin
operator|.
name|class
operator|.
name|getName
argument_list|()
operator|+
literal|".Send"
argument_list|)
decl_stmt|;
specifier|private
name|Log
name|ackLog
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|LoggingBrokerPlugin
operator|.
name|class
operator|.
name|getName
argument_list|()
operator|+
literal|".Ack"
argument_list|)
decl_stmt|;
specifier|public
name|void
name|send
parameter_list|(
name|ProducerBrokerExchange
name|producerExchange
parameter_list|,
name|Message
name|messageSend
parameter_list|)
throws|throws
name|Exception
block|{
if|if
condition|(
name|sendLog
operator|.
name|isInfoEnabled
argument_list|()
condition|)
block|{
name|sendLog
operator|.
name|info
argument_list|(
literal|"Sending: "
operator|+
name|messageSend
argument_list|)
expr_stmt|;
block|}
name|super
operator|.
name|send
argument_list|(
name|producerExchange
argument_list|,
name|messageSend
argument_list|)
expr_stmt|;
block|}
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
if|if
condition|(
name|ackLog
operator|.
name|isInfoEnabled
argument_list|()
condition|)
block|{
name|ackLog
operator|.
name|info
argument_list|(
literal|"Acknowledge: "
operator|+
name|ack
argument_list|)
expr_stmt|;
block|}
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
comment|// Properties
comment|// -------------------------------------------------------------------------
specifier|public
name|Log
name|getAckLog
parameter_list|()
block|{
return|return
name|ackLog
return|;
block|}
specifier|public
name|void
name|setAckLog
parameter_list|(
name|Log
name|ackLog
parameter_list|)
block|{
name|this
operator|.
name|ackLog
operator|=
name|ackLog
expr_stmt|;
block|}
specifier|public
name|Log
name|getLog
parameter_list|()
block|{
return|return
name|log
return|;
block|}
specifier|public
name|void
name|setLog
parameter_list|(
name|Log
name|log
parameter_list|)
block|{
name|this
operator|.
name|log
operator|=
name|log
expr_stmt|;
block|}
specifier|public
name|Log
name|getSendLog
parameter_list|()
block|{
return|return
name|sendLog
return|;
block|}
specifier|public
name|void
name|setSendLog
parameter_list|(
name|Log
name|sendLog
parameter_list|)
block|{
name|this
operator|.
name|sendLog
operator|=
name|sendLog
expr_stmt|;
block|}
block|}
end_class

end_unit

