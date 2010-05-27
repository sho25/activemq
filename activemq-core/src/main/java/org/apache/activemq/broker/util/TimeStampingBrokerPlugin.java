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
comment|/**  * A Broker interceptor which updates a JMS Client's timestamp on the message  * with a broker timestamp. Useful when the clocks on client machines are known  * to not be correct and you can only trust the time set on the broker machines.  *   * Enabling this plugin will break JMS compliance since the timestamp that the  * producer sees on the messages after as send() will be different from the  * timestamp the consumer will observe when he receives the message. This plugin  * is not enabled in the default ActiveMQ configuration.  *   * 2 new attributes have been added which will allow the administrator some override control  * over the expiration time for incoming messages:  *  * Attribute 'zeroExpirationOverride' can be used to apply an expiration  * time to incoming messages with no expiration defined (messages that would never expire)  *  * Attribute 'ttlCeiling' can be used to apply a limit to the expiration time  *  * @org.apache.xbean.XBean element="timeStampingBrokerPlugin"  *   * @version $Revision$  */
end_comment

begin_class
specifier|public
class|class
name|TimeStampingBrokerPlugin
extends|extends
name|BrokerPluginSupport
block|{
specifier|private
specifier|static
specifier|final
name|Log
name|LOG
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|TimeStampingBrokerPlugin
operator|.
name|class
argument_list|)
decl_stmt|;
comment|/**      * variable which (when non-zero) is used to override     * the expiration date for messages that arrive with     * no expiration date set (in Milliseconds).     */
name|long
name|zeroExpirationOverride
init|=
literal|0
decl_stmt|;
comment|/**      * variable which (when non-zero) is used to limit     * the expiration date (in Milliseconds).       */
name|long
name|ttlCeiling
init|=
literal|0
decl_stmt|;
comment|/**      * If true, the plugin will not update timestamp to past values      * False by default      */
name|boolean
name|futureOnly
init|=
literal|false
decl_stmt|;
comment|/**      * if true, update timestamp even if message has passed through a network      * default false      */
name|boolean
name|processNetworkMessages
init|=
literal|false
decl_stmt|;
comment|/**      * setter method for zeroExpirationOverride     */
specifier|public
name|void
name|setZeroExpirationOverride
parameter_list|(
name|long
name|ttl
parameter_list|)
block|{
name|this
operator|.
name|zeroExpirationOverride
operator|=
name|ttl
expr_stmt|;
block|}
comment|/**      * setter method for ttlCeiling     */
specifier|public
name|void
name|setTtlCeiling
parameter_list|(
name|long
name|ttlCeiling
parameter_list|)
block|{
name|this
operator|.
name|ttlCeiling
operator|=
name|ttlCeiling
expr_stmt|;
block|}
specifier|public
name|void
name|setFutureOnly
parameter_list|(
name|boolean
name|futureOnly
parameter_list|)
block|{
name|this
operator|.
name|futureOnly
operator|=
name|futureOnly
expr_stmt|;
block|}
specifier|public
name|void
name|setProcessNetworkMessages
parameter_list|(
name|Boolean
name|processNetworkMessages
parameter_list|)
block|{
name|this
operator|.
name|processNetworkMessages
operator|=
name|processNetworkMessages
expr_stmt|;
block|}
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
if|if
condition|(
name|message
operator|.
name|getTimestamp
argument_list|()
operator|>
literal|0
operator|&&
operator|(
name|processNetworkMessages
operator|||
operator|(
name|message
operator|.
name|getBrokerPath
argument_list|()
operator|==
literal|null
operator|||
name|message
operator|.
name|getBrokerPath
argument_list|()
operator|.
name|length
operator|==
literal|0
operator|)
operator|)
condition|)
block|{
comment|// timestamp not been disabled and has not passed through a network or processNetworkMessages=true
name|long
name|oldExpiration
init|=
name|message
operator|.
name|getExpiration
argument_list|()
decl_stmt|;
name|long
name|newTimeStamp
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
name|long
name|timeToLive
init|=
name|zeroExpirationOverride
decl_stmt|;
name|long
name|oldTimestamp
init|=
name|message
operator|.
name|getTimestamp
argument_list|()
decl_stmt|;
if|if
condition|(
name|oldExpiration
operator|>
literal|0
condition|)
block|{
name|timeToLive
operator|=
name|oldExpiration
operator|-
name|oldTimestamp
expr_stmt|;
block|}
if|if
condition|(
name|timeToLive
operator|>
literal|0
operator|&&
name|ttlCeiling
operator|>
literal|0
operator|&&
name|timeToLive
operator|>
name|ttlCeiling
condition|)
block|{
name|timeToLive
operator|=
name|ttlCeiling
expr_stmt|;
block|}
name|long
name|expiration
init|=
name|timeToLive
operator|+
name|newTimeStamp
decl_stmt|;
comment|//In the scenario that the Broker is behind the clients we never want to set the Timestamp and Expiration in the past
if|if
condition|(
operator|!
name|futureOnly
operator|||
operator|(
name|expiration
operator|>
name|oldExpiration
operator|)
condition|)
block|{
if|if
condition|(
name|timeToLive
operator|>
literal|0
operator|&&
name|expiration
operator|>
literal|0
condition|)
block|{
name|message
operator|.
name|setExpiration
argument_list|(
name|expiration
argument_list|)
expr_stmt|;
block|}
name|message
operator|.
name|setTimestamp
argument_list|(
name|newTimeStamp
argument_list|)
expr_stmt|;
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Set message "
operator|+
name|message
operator|.
name|getMessageId
argument_list|()
operator|+
literal|" timestamp from "
operator|+
name|oldTimestamp
operator|+
literal|" to "
operator|+
name|newTimeStamp
argument_list|)
expr_stmt|;
block|}
block|}
block|}
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
block|}
end_class

end_unit

