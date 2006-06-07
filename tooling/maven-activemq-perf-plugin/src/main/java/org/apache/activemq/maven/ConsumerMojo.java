begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
package|package
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|maven
package|;
end_package

begin_comment
comment|/*  * Copyright 2001-2005 The Apache Software Foundation.  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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
name|JmsConsumerSystem
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|maven
operator|.
name|plugin
operator|.
name|AbstractMojo
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|maven
operator|.
name|plugin
operator|.
name|MojoExecutionException
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

begin_comment
comment|/**  * Goal which touches a timestamp file.  *  * @goal consumer  * @phase process-sources  */
end_comment

begin_class
specifier|public
class|class
name|ConsumerMojo
extends|extends
name|AbstractMojo
block|{
comment|/**      * @parameter expression="${sampler.duration}" default-value="60000"      * @required      */
specifier|private
name|String
name|duration
decl_stmt|;
comment|/**      * @parameter expression="${sampler.interval}" default-value="1000"      * @required      */
specifier|private
name|String
name|interval
decl_stmt|;
comment|/**      * @parameter expression="${sampler.rampUpTime}" default-value="10000"      * @required      */
specifier|private
name|String
name|rampUpTime
decl_stmt|;
comment|/**      * @parameter expression="${sampler.rampDownTime}" default-value="10000"      * @required      */
specifier|private
name|String
name|rampDownTime
decl_stmt|;
comment|/**      * @parameter expression="${consumer.spiClass}" default-value="org.apache.activemq.tool.spi.ActiveMQPojoSPI"      * @required      */
specifier|private
name|String
name|spiClass
decl_stmt|;
comment|/**      * @parameter expression="${consumer.sessTransacted}" default-value="false"      * @required      */
specifier|private
name|String
name|sessTransacted
decl_stmt|;
comment|/**      * @parameter expression="${consumer.sessAckMode}" default-value="autoAck"      * @required      */
specifier|private
name|String
name|sessAckMode
decl_stmt|;
comment|/**      * @parameter expression="${consumer.destName}" default-value="topic://TEST.PERFORMANCE.FOO.BAR"      * @required      */
specifier|private
name|String
name|destName
decl_stmt|;
comment|/**      * @parameter expression="${consumer.destCount}" default-value="1"      * @required      */
specifier|private
name|String
name|destCount
decl_stmt|;
comment|/**      * @parameter expression="${consumer.destComposite}" default-value="false"      * @required      */
specifier|private
name|String
name|destComposite
decl_stmt|;
comment|/**      * @parameter expression="${consumer.durable}" default-value="false"      * @required      */
specifier|private
name|String
name|durable
decl_stmt|;
comment|/**      * @parameter expression="${consumer.asyncRecv}" default-value="true"      * @required      */
specifier|private
name|String
name|asyncRecv
decl_stmt|;
comment|/**      * @parameter expression="${consumer.recvCount}" default-value="1000000"      * @required      */
specifier|private
name|String
name|recvCount
decl_stmt|;
comment|/*      * @parameter expression="${consumer.recvDuration}" default-value="60000"      * @required      private String recvDuration;     */
comment|/**      * @parameter expression="${consumer.recvType}" default-value="time"      * @required      */
specifier|private
name|String
name|recvType
decl_stmt|;
comment|/**      * @parameter expression="${factory.brokerUrl}" default-value="tcp://localhost:61616"      * @required      */
specifier|private
name|String
name|brokerUrl
decl_stmt|;
comment|/**      * @parameter expression="${factory.optimAck}" default-value="true"      * @required      */
specifier|private
name|String
name|optimAck
decl_stmt|;
comment|/**      * @parameter expression="${factory.optimDispatch}" default-value="true"      * @required      */
specifier|private
name|String
name|optimDispatch
decl_stmt|;
comment|/**      * @parameter expression="${factory.prefetchQueue}" default-value="5000"      * @required      */
specifier|private
name|String
name|prefetchQueue
decl_stmt|;
comment|/**      * @parameter expression="${factory.prefetchTopic}" default-value="5000"      * @required      */
specifier|private
name|String
name|prefetchTopic
decl_stmt|;
comment|/**      * @parameter expression="${factory.useRetroactive}" default-value="false"      * @required      */
specifier|private
name|String
name|useRetroactive
decl_stmt|;
comment|/**      * @parameter expression="${sysTest.numClients}" default-value="1"      * @required      */
specifier|private
name|String
name|numClients
decl_stmt|;
comment|/**      * @parameter expression="${sysTest.totalDests}" default-value="1"      * @required      */
specifier|private
name|String
name|totalDests
decl_stmt|;
comment|/**      * @parameter expression="${sysTest.destDistro}" default-value="all"      * @required      */
specifier|private
name|String
name|destDistro
decl_stmt|;
comment|/**      * @parameter expression="${sysTest.reportDirectory}" default-value="${project.build.directory}/test-perf"      * @required      */
specifier|private
name|String
name|reportDirectory
decl_stmt|;
specifier|public
name|void
name|execute
parameter_list|()
throws|throws
name|MojoExecutionException
block|{
try|try
block|{
name|JmsConsumerSystem
operator|.
name|main
argument_list|(
name|createArgument
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|JMSException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|MojoExecutionException
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
throw|;
block|}
block|}
specifier|public
name|String
index|[]
name|createArgument
parameter_list|()
block|{
name|String
index|[]
name|options
init|=
block|{
literal|"sampler.duration="
operator|+
name|duration
block|,
literal|"sampler.interval="
operator|+
name|interval
block|,
literal|"sampler.rampUpTime="
operator|+
name|rampUpTime
block|,
literal|"sampler.rampDownTime="
operator|+
name|rampDownTime
block|,
literal|"consumer.spiClass="
operator|+
name|spiClass
block|,
literal|"consumer.sessTransacted="
operator|+
name|sessTransacted
block|,
literal|"consumer.sessAckMode="
operator|+
name|sessAckMode
block|,
literal|"consumer.destName="
operator|+
name|destName
block|,
literal|"consumer.destCount="
operator|+
name|destCount
block|,
literal|"consumer.destComposite="
operator|+
name|destComposite
block|,
literal|"consumer.durable="
operator|+
name|durable
block|,
literal|"consumer.asyncRecv="
operator|+
name|asyncRecv
block|,
literal|"consumer.recvCount="
operator|+
name|recvCount
block|,
literal|"consumer.recvDuration="
operator|+
name|duration
block|,
literal|"consumer.recvType="
operator|+
name|recvType
block|,
literal|"factory.brokerUrl="
operator|+
name|brokerUrl
block|,
literal|"factory.optimAck="
operator|+
name|optimAck
block|,
literal|"factory.optimDispatch="
operator|+
name|optimDispatch
block|,
literal|"factory.prefetchQueue="
operator|+
name|prefetchQueue
block|,
literal|"factory.prefetchTopic="
operator|+
name|prefetchTopic
block|,
literal|"factory.useRetroactive="
operator|+
name|useRetroactive
block|,
literal|"sysTest.numClients="
operator|+
name|numClients
block|,
literal|"sysTest.totalDests="
operator|+
name|totalDests
block|,
literal|"sysTest.destDistro="
operator|+
name|destDistro
block|,
literal|"sysTest.reportDirectory="
operator|+
name|reportDirectory
block|}
decl_stmt|;
return|return
name|options
return|;
block|}
block|}
end_class

end_unit

