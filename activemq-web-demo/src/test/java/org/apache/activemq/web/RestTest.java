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
name|web
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
name|TextMessage
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
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|lang
operator|.
name|RandomStringUtils
import|;
end_import

begin_import
import|import
name|org
operator|.
name|eclipse
operator|.
name|jetty
operator|.
name|client
operator|.
name|ContentExchange
import|;
end_import

begin_import
import|import
name|org
operator|.
name|eclipse
operator|.
name|jetty
operator|.
name|client
operator|.
name|HttpClient
import|;
end_import

begin_import
import|import
name|org
operator|.
name|eclipse
operator|.
name|jetty
operator|.
name|http
operator|.
name|HttpFields
import|;
end_import

begin_import
import|import
name|org
operator|.
name|eclipse
operator|.
name|jetty
operator|.
name|http
operator|.
name|HttpStatus
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
name|*
import|;
end_import

begin_class
specifier|public
class|class
name|RestTest
extends|extends
name|JettyTestSupport
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
name|RestTest
operator|.
name|class
argument_list|)
decl_stmt|;
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|60
operator|*
literal|1000
argument_list|)
specifier|public
name|void
name|testConsume
parameter_list|()
throws|throws
name|Exception
block|{
name|int
name|port
init|=
name|getPort
argument_list|()
decl_stmt|;
name|producer
operator|.
name|send
argument_list|(
name|session
operator|.
name|createTextMessage
argument_list|(
literal|"test"
argument_list|)
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"message sent"
argument_list|)
expr_stmt|;
name|HttpClient
name|httpClient
init|=
operator|new
name|HttpClient
argument_list|()
decl_stmt|;
name|httpClient
operator|.
name|start
argument_list|()
expr_stmt|;
name|ContentExchange
name|contentExchange
init|=
operator|new
name|ContentExchange
argument_list|()
decl_stmt|;
name|httpClient
operator|.
name|setConnectorType
argument_list|(
name|HttpClient
operator|.
name|CONNECTOR_SELECT_CHANNEL
argument_list|)
expr_stmt|;
name|contentExchange
operator|.
name|setURL
argument_list|(
literal|"http://localhost:"
operator|+
name|port
operator|+
literal|"/message/test?readTimeout=1000&type=queue"
argument_list|)
expr_stmt|;
name|httpClient
operator|.
name|send
argument_list|(
name|contentExchange
argument_list|)
expr_stmt|;
name|contentExchange
operator|.
name|waitForDone
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|"test"
argument_list|,
name|contentExchange
operator|.
name|getResponseContent
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|60
operator|*
literal|1000
argument_list|)
specifier|public
name|void
name|testSubscribeFirst
parameter_list|()
throws|throws
name|Exception
block|{
name|int
name|port
init|=
name|getPort
argument_list|()
decl_stmt|;
name|HttpClient
name|httpClient
init|=
operator|new
name|HttpClient
argument_list|()
decl_stmt|;
name|httpClient
operator|.
name|start
argument_list|()
expr_stmt|;
name|ContentExchange
name|contentExchange
init|=
operator|new
name|ContentExchange
argument_list|()
decl_stmt|;
name|httpClient
operator|.
name|setConnectorType
argument_list|(
name|HttpClient
operator|.
name|CONNECTOR_SELECT_CHANNEL
argument_list|)
expr_stmt|;
name|contentExchange
operator|.
name|setURL
argument_list|(
literal|"http://localhost:"
operator|+
name|port
operator|+
literal|"/message/test?readTimeout=5000&type=queue"
argument_list|)
expr_stmt|;
name|httpClient
operator|.
name|send
argument_list|(
name|contentExchange
argument_list|)
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
name|producer
operator|.
name|send
argument_list|(
name|session
operator|.
name|createTextMessage
argument_list|(
literal|"test"
argument_list|)
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"message sent"
argument_list|)
expr_stmt|;
name|contentExchange
operator|.
name|waitForDone
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|"test"
argument_list|,
name|contentExchange
operator|.
name|getResponseContent
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|60
operator|*
literal|1000
argument_list|)
specifier|public
name|void
name|testSelector
parameter_list|()
throws|throws
name|Exception
block|{
name|int
name|port
init|=
name|getPort
argument_list|()
decl_stmt|;
name|TextMessage
name|msg1
init|=
name|session
operator|.
name|createTextMessage
argument_list|(
literal|"test1"
argument_list|)
decl_stmt|;
name|msg1
operator|.
name|setIntProperty
argument_list|(
literal|"test"
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|producer
operator|.
name|send
argument_list|(
name|msg1
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"message 1 sent"
argument_list|)
expr_stmt|;
name|TextMessage
name|msg2
init|=
name|session
operator|.
name|createTextMessage
argument_list|(
literal|"test2"
argument_list|)
decl_stmt|;
name|msg2
operator|.
name|setIntProperty
argument_list|(
literal|"test"
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|producer
operator|.
name|send
argument_list|(
name|msg2
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"message 2 sent"
argument_list|)
expr_stmt|;
name|HttpClient
name|httpClient
init|=
operator|new
name|HttpClient
argument_list|()
decl_stmt|;
name|httpClient
operator|.
name|start
argument_list|()
expr_stmt|;
name|ContentExchange
name|contentExchange
init|=
operator|new
name|ContentExchange
argument_list|()
decl_stmt|;
name|httpClient
operator|.
name|setConnectorType
argument_list|(
name|HttpClient
operator|.
name|CONNECTOR_SELECT_CHANNEL
argument_list|)
expr_stmt|;
name|contentExchange
operator|.
name|setURL
argument_list|(
literal|"http://localhost:"
operator|+
name|port
operator|+
literal|"/message/test?readTimeout=1000&type=queue"
argument_list|)
expr_stmt|;
name|contentExchange
operator|.
name|setRequestHeader
argument_list|(
literal|"selector"
argument_list|,
literal|"test=2"
argument_list|)
expr_stmt|;
name|httpClient
operator|.
name|send
argument_list|(
name|contentExchange
argument_list|)
expr_stmt|;
name|contentExchange
operator|.
name|waitForDone
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|"test2"
argument_list|,
name|contentExchange
operator|.
name|getResponseContent
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// test for https://issues.apache.org/activemq/browse/AMQ-2827
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|15
operator|*
literal|1000
argument_list|)
specifier|public
name|void
name|testCorrelation
parameter_list|()
throws|throws
name|Exception
block|{
name|int
name|port
init|=
name|getPort
argument_list|()
decl_stmt|;
name|HttpClient
name|httpClient
init|=
operator|new
name|HttpClient
argument_list|()
decl_stmt|;
name|httpClient
operator|.
name|setConnectorType
argument_list|(
name|HttpClient
operator|.
name|CONNECTOR_SELECT_CHANNEL
argument_list|)
expr_stmt|;
name|httpClient
operator|.
name|start
argument_list|()
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
literal|200
condition|;
name|i
operator|++
control|)
block|{
name|String
name|correlId
init|=
literal|"RESTY"
operator|+
name|RandomStringUtils
operator|.
name|randomNumeric
argument_list|(
literal|10
argument_list|)
decl_stmt|;
name|TextMessage
name|message
init|=
name|session
operator|.
name|createTextMessage
argument_list|(
name|correlId
argument_list|)
decl_stmt|;
name|message
operator|.
name|setStringProperty
argument_list|(
literal|"correlationId"
argument_list|,
name|correlId
argument_list|)
expr_stmt|;
name|message
operator|.
name|setJMSCorrelationID
argument_list|(
name|correlId
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Sending: "
operator|+
name|correlId
argument_list|)
expr_stmt|;
name|producer
operator|.
name|send
argument_list|(
name|message
argument_list|)
expr_stmt|;
name|ContentExchange
name|contentExchange
init|=
operator|new
name|ContentExchange
argument_list|()
decl_stmt|;
name|contentExchange
operator|.
name|setURL
argument_list|(
literal|"http://localhost:"
operator|+
name|port
operator|+
literal|"/message/test?readTimeout=1000&type=queue&clientId=test"
argument_list|)
expr_stmt|;
name|httpClient
operator|.
name|send
argument_list|(
name|contentExchange
argument_list|)
expr_stmt|;
name|contentExchange
operator|.
name|waitForDone
argument_list|()
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Received: ["
operator|+
name|contentExchange
operator|.
name|getResponseStatus
argument_list|()
operator|+
literal|"] "
operator|+
name|contentExchange
operator|.
name|getResponseContent
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|200
argument_list|,
name|contentExchange
operator|.
name|getResponseStatus
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|correlId
argument_list|,
name|contentExchange
operator|.
name|getResponseContent
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|httpClient
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|15
operator|*
literal|1000
argument_list|)
specifier|public
name|void
name|testDisconnect
parameter_list|()
throws|throws
name|Exception
block|{
name|int
name|port
init|=
name|getPort
argument_list|()
decl_stmt|;
name|producer
operator|.
name|send
argument_list|(
name|session
operator|.
name|createTextMessage
argument_list|(
literal|"test"
argument_list|)
argument_list|)
expr_stmt|;
name|HttpClient
name|httpClient
init|=
operator|new
name|HttpClient
argument_list|()
decl_stmt|;
name|httpClient
operator|.
name|start
argument_list|()
expr_stmt|;
name|ContentExchange
name|contentExchange
init|=
operator|new
name|ContentExchange
argument_list|()
decl_stmt|;
name|httpClient
operator|.
name|setConnectorType
argument_list|(
name|HttpClient
operator|.
name|CONNECTOR_SELECT_CHANNEL
argument_list|)
expr_stmt|;
name|contentExchange
operator|.
name|setURL
argument_list|(
literal|"http://localhost:"
operator|+
name|port
operator|+
literal|"/message/test?readTimeout=1000&type=queue&clientId=test"
argument_list|)
expr_stmt|;
name|httpClient
operator|.
name|send
argument_list|(
name|contentExchange
argument_list|)
expr_stmt|;
name|contentExchange
operator|.
name|waitForDone
argument_list|()
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Received: ["
operator|+
name|contentExchange
operator|.
name|getResponseStatus
argument_list|()
operator|+
literal|"] "
operator|+
name|contentExchange
operator|.
name|getResponseContent
argument_list|()
argument_list|)
expr_stmt|;
name|contentExchange
operator|=
operator|new
name|ContentExchange
argument_list|()
expr_stmt|;
name|contentExchange
operator|.
name|setMethod
argument_list|(
literal|"POST"
argument_list|)
expr_stmt|;
name|contentExchange
operator|.
name|setURL
argument_list|(
literal|"http://localhost:"
operator|+
name|port
operator|+
literal|"/message/test?clientId=test&action=unsubscribe"
argument_list|)
expr_stmt|;
name|httpClient
operator|.
name|send
argument_list|(
name|contentExchange
argument_list|)
expr_stmt|;
name|contentExchange
operator|.
name|waitForDone
argument_list|()
expr_stmt|;
name|httpClient
operator|.
name|stop
argument_list|()
expr_stmt|;
name|ObjectName
name|query
init|=
operator|new
name|ObjectName
argument_list|(
literal|"org.apache.activemq:BrokerName=localhost,Type=Subscription,destinationType=Queue,destinationName=test,*"
argument_list|)
decl_stmt|;
name|Set
argument_list|<
name|ObjectName
argument_list|>
name|subs
init|=
name|broker
operator|.
name|getManagementContext
argument_list|()
operator|.
name|queryNames
argument_list|(
name|query
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"Consumers not closed"
argument_list|,
literal|0
argument_list|,
name|subs
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|15
operator|*
literal|1000
argument_list|)
specifier|public
name|void
name|testPost
parameter_list|()
throws|throws
name|Exception
block|{
name|int
name|port
init|=
name|getPort
argument_list|()
decl_stmt|;
name|HttpClient
name|httpClient
init|=
operator|new
name|HttpClient
argument_list|()
decl_stmt|;
name|httpClient
operator|.
name|start
argument_list|()
expr_stmt|;
name|ContentExchange
name|contentExchange
init|=
operator|new
name|ContentExchange
argument_list|()
decl_stmt|;
name|httpClient
operator|.
name|setConnectorType
argument_list|(
name|HttpClient
operator|.
name|CONNECTOR_SELECT_CHANNEL
argument_list|)
expr_stmt|;
name|contentExchange
operator|.
name|setMethod
argument_list|(
literal|"POST"
argument_list|)
expr_stmt|;
name|contentExchange
operator|.
name|setURL
argument_list|(
literal|"http://localhost:"
operator|+
name|port
operator|+
literal|"/message/testPost?type=queue"
argument_list|)
expr_stmt|;
name|httpClient
operator|.
name|send
argument_list|(
name|contentExchange
argument_list|)
expr_stmt|;
name|contentExchange
operator|.
name|waitForDone
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
literal|"success status"
argument_list|,
name|HttpStatus
operator|.
name|isSuccess
argument_list|(
name|contentExchange
operator|.
name|getResponseStatus
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|ContentExchange
name|contentExchange2
init|=
operator|new
name|ContentExchange
argument_list|()
decl_stmt|;
name|contentExchange2
operator|.
name|setURL
argument_list|(
literal|"http://localhost:"
operator|+
name|port
operator|+
literal|"/message/testPost?readTimeout=1000&type=Queue"
argument_list|)
expr_stmt|;
name|httpClient
operator|.
name|send
argument_list|(
name|contentExchange2
argument_list|)
expr_stmt|;
name|contentExchange2
operator|.
name|waitForDone
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
literal|"success status"
argument_list|,
name|HttpStatus
operator|.
name|isSuccess
argument_list|(
name|contentExchange2
operator|.
name|getResponseStatus
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// test for https://issues.apache.org/activemq/browse/AMQ-3857
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|15
operator|*
literal|1000
argument_list|)
specifier|public
name|void
name|testProperties
parameter_list|()
throws|throws
name|Exception
block|{
name|int
name|port
init|=
name|getPort
argument_list|()
decl_stmt|;
name|HttpClient
name|httpClient
init|=
operator|new
name|HttpClient
argument_list|()
decl_stmt|;
name|httpClient
operator|.
name|start
argument_list|()
expr_stmt|;
name|ContentExchange
name|contentExchange
init|=
operator|new
name|ContentExchange
argument_list|()
decl_stmt|;
name|httpClient
operator|.
name|setConnectorType
argument_list|(
name|HttpClient
operator|.
name|CONNECTOR_SELECT_CHANNEL
argument_list|)
expr_stmt|;
name|contentExchange
operator|.
name|setMethod
argument_list|(
literal|"POST"
argument_list|)
expr_stmt|;
name|contentExchange
operator|.
name|setURL
argument_list|(
literal|"http://localhost:"
operator|+
name|port
operator|+
literal|"/message/testPost?type=queue&property=value"
argument_list|)
expr_stmt|;
name|httpClient
operator|.
name|send
argument_list|(
name|contentExchange
argument_list|)
expr_stmt|;
name|contentExchange
operator|.
name|waitForDone
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
literal|"success status"
argument_list|,
name|HttpStatus
operator|.
name|isSuccess
argument_list|(
name|contentExchange
operator|.
name|getResponseStatus
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|ContentExchange
name|contentExchange2
init|=
operator|new
name|ContentExchange
argument_list|(
literal|true
argument_list|)
decl_stmt|;
name|contentExchange2
operator|.
name|setURL
argument_list|(
literal|"http://localhost:"
operator|+
name|port
operator|+
literal|"/message/testPost?readTimeout=1000&type=Queue"
argument_list|)
expr_stmt|;
name|httpClient
operator|.
name|send
argument_list|(
name|contentExchange2
argument_list|)
expr_stmt|;
name|contentExchange2
operator|.
name|waitForDone
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
literal|"success status"
argument_list|,
name|HttpStatus
operator|.
name|isSuccess
argument_list|(
name|contentExchange2
operator|.
name|getResponseStatus
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|HttpFields
name|fields
init|=
name|contentExchange2
operator|.
name|getResponseFields
argument_list|()
decl_stmt|;
name|assertNotNull
argument_list|(
literal|"Headers Exist"
argument_list|,
name|fields
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"header value"
argument_list|,
literal|"value"
argument_list|,
name|fields
operator|.
name|getStringField
argument_list|(
literal|"property"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|15
operator|*
literal|1000
argument_list|)
specifier|public
name|void
name|testAuth
parameter_list|()
throws|throws
name|Exception
block|{
name|int
name|port
init|=
name|getPort
argument_list|()
decl_stmt|;
name|HttpClient
name|httpClient
init|=
operator|new
name|HttpClient
argument_list|()
decl_stmt|;
name|httpClient
operator|.
name|start
argument_list|()
expr_stmt|;
name|ContentExchange
name|contentExchange
init|=
operator|new
name|ContentExchange
argument_list|()
decl_stmt|;
name|httpClient
operator|.
name|setConnectorType
argument_list|(
name|HttpClient
operator|.
name|CONNECTOR_SELECT_CHANNEL
argument_list|)
expr_stmt|;
name|contentExchange
operator|.
name|setMethod
argument_list|(
literal|"POST"
argument_list|)
expr_stmt|;
name|contentExchange
operator|.
name|setURL
argument_list|(
literal|"http://localhost:"
operator|+
name|port
operator|+
literal|"/message/testPost?type=queue"
argument_list|)
expr_stmt|;
name|contentExchange
operator|.
name|setRequestHeader
argument_list|(
literal|"Authorization"
argument_list|,
literal|"Basic YWRtaW46YWRtaW4="
argument_list|)
expr_stmt|;
name|httpClient
operator|.
name|send
argument_list|(
name|contentExchange
argument_list|)
expr_stmt|;
name|contentExchange
operator|.
name|waitForDone
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
literal|"success status"
argument_list|,
name|HttpStatus
operator|.
name|isSuccess
argument_list|(
name|contentExchange
operator|.
name|getResponseStatus
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

