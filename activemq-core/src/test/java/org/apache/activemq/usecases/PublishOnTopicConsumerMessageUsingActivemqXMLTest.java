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
name|usecases
package|;
end_package

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
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|ActiveMQConnectionFactory
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
name|xbean
operator|.
name|BrokerFactoryBean
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
name|ClassPathResource
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

begin_comment
comment|/**  * Test Publish/Consume topic  using the release activemq.xml configuration file  *  *   */
end_comment

begin_class
specifier|public
class|class
name|PublishOnTopicConsumerMessageUsingActivemqXMLTest
extends|extends
name|PublishOnTopicConsumedMessageTest
block|{
specifier|protected
specifier|static
specifier|final
name|String
name|JOURNAL_ROOT
init|=
literal|"../data/"
decl_stmt|;
specifier|private
specifier|static
specifier|final
specifier|transient
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|PublishOnTopicConsumerMessageUsingActivemqXMLTest
operator|.
name|class
argument_list|)
decl_stmt|;
name|BrokerService
name|broker
decl_stmt|;
comment|/**      * Use the transportConnector uri configured on the activemq.xml      *      * @return ActiveMQConnectionFactory      * @throws Exception      */
specifier|protected
name|ActiveMQConnectionFactory
name|createConnectionFactory
parameter_list|()
throws|throws
name|Exception
block|{
return|return
operator|new
name|ActiveMQConnectionFactory
argument_list|(
literal|"tcp://localhost:61616"
argument_list|)
return|;
block|}
comment|/**      * Sets up a test where the producer and consumer have their own connection.      *      * @see junit.framework.TestCase#setUp()      */
specifier|protected
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|File
name|journalFile
init|=
operator|new
name|File
argument_list|(
name|JOURNAL_ROOT
argument_list|)
decl_stmt|;
name|recursiveDelete
argument_list|(
name|journalFile
argument_list|)
expr_stmt|;
comment|// Create broker from resource
name|LOG
operator|.
name|info
argument_list|(
literal|"Creating broker... "
argument_list|)
expr_stmt|;
name|broker
operator|=
name|createBroker
argument_list|(
literal|"org/apache/activemq/usecases/activemq.xml"
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Success"
argument_list|)
expr_stmt|;
name|super
operator|.
name|setUp
argument_list|()
expr_stmt|;
block|}
comment|/*     * Stops the Broker     * @see junit.framework.TestCase#tearDown()     */
specifier|protected
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Closing Broker"
argument_list|)
expr_stmt|;
if|if
condition|(
name|broker
operator|!=
literal|null
condition|)
block|{
name|broker
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
name|LOG
operator|.
name|info
argument_list|(
literal|"Broker closed..."
argument_list|)
expr_stmt|;
block|}
comment|/*     * clean up the journal     */
specifier|protected
specifier|static
name|void
name|recursiveDelete
parameter_list|(
name|File
name|file
parameter_list|)
block|{
if|if
condition|(
name|file
operator|.
name|isDirectory
argument_list|()
condition|)
block|{
name|File
index|[]
name|files
init|=
name|file
operator|.
name|listFiles
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|files
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|recursiveDelete
argument_list|(
name|files
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
block|}
name|file
operator|.
name|delete
argument_list|()
expr_stmt|;
block|}
specifier|protected
name|BrokerService
name|createBroker
parameter_list|(
name|String
name|resource
parameter_list|)
throws|throws
name|Exception
block|{
return|return
name|createBroker
argument_list|(
operator|new
name|ClassPathResource
argument_list|(
name|resource
argument_list|)
argument_list|)
return|;
block|}
specifier|protected
name|BrokerService
name|createBroker
parameter_list|(
name|Resource
name|resource
parameter_list|)
throws|throws
name|Exception
block|{
name|BrokerFactoryBean
name|factory
init|=
operator|new
name|BrokerFactoryBean
argument_list|(
name|resource
argument_list|)
decl_stmt|;
name|factory
operator|.
name|afterPropertiesSet
argument_list|()
expr_stmt|;
name|BrokerService
name|broker
init|=
name|factory
operator|.
name|getBroker
argument_list|()
decl_stmt|;
comment|//assertTrue("Should have a broker!", broker != null);
return|return
name|broker
return|;
block|}
block|}
end_class

end_unit

