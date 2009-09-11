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
name|BrokerPlugin
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
comment|/**  * A StatisticsBrokerPlugin  * You can retrieve a Map Message for a Destination - or  * Broker containing statistics as key-value pairs The message must contain a  * replyTo Destination - else its ignored  * To retrieve stats on the broker send a empty message to ActiveMQ.Statistics.Broker (Queue or Topic)  * With a replyTo set to the destination you want the stats returned to.  * To retrieve stats for a destination - e.g. foo - send an empty message to ActiveMQ.Statistics.Destination.foo  * - this works with wildcards to - you get a message for each wildcard match on the replyTo destination.  * The stats message is a MapMessage populated with statistics for the target  * @org.apache.xbean.XBean element="statisticsBrokerPlugin"  *  */
end_comment

begin_class
specifier|public
class|class
name|StatisticsBrokerPlugin
implements|implements
name|BrokerPlugin
block|{
specifier|private
specifier|static
name|Log
name|LOG
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|StatisticsBrokerPlugin
operator|.
name|class
argument_list|)
decl_stmt|;
comment|/**       * @param broker      * @return the plug-in      * @throws Exception      * @see org.apache.activemq.broker.BrokerPlugin#installPlugin(org.apache.activemq.broker.Broker)      */
specifier|public
name|Broker
name|installPlugin
parameter_list|(
name|Broker
name|broker
parameter_list|)
throws|throws
name|Exception
block|{
name|StatisticsBroker
name|answer
init|=
operator|new
name|StatisticsBroker
argument_list|(
name|broker
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Installing StaticsBroker"
argument_list|)
expr_stmt|;
return|return
name|answer
return|;
block|}
block|}
end_class

end_unit

