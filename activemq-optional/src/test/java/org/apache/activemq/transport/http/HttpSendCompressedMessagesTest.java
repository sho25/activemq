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
name|transport
operator|.
name|http
package|;
end_package

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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|UUID
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
name|javax
operator|.
name|jms
operator|.
name|BytesMessage
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|MapMessage
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|MessageConsumer
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|MessageProducer
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|Session
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|StreamMessage
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|Topic
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
name|broker
operator|.
name|TransportConnector
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
name|ActiveMQBytesMessage
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
name|ActiveMQMapMessage
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
name|ActiveMQStreamMessage
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
name|ActiveMQTextMessage
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
name|ByteSequence
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|After
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Before
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

begin_class
specifier|public
class|class
name|HttpSendCompressedMessagesTest
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
name|HttpSendCompressedMessagesTest
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
name|BrokerService
name|broker
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|tcpBindAddress
init|=
literal|"tcp://0.0.0.0:0"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|httpBindAddress
init|=
literal|"http://0.0.0.0:8171"
decl_stmt|;
specifier|private
name|ActiveMQConnectionFactory
name|tcpConnectionFactory
decl_stmt|;
specifier|private
name|ActiveMQConnectionFactory
name|httpConnectionFactory
decl_stmt|;
specifier|private
name|ActiveMQConnection
name|tcpConnection
decl_stmt|;
specifier|private
name|ActiveMQConnection
name|httpConnection
decl_stmt|;
specifier|private
name|Session
name|tcpSession
decl_stmt|;
specifier|private
name|Session
name|httpSession
decl_stmt|;
specifier|private
name|Topic
name|destination
decl_stmt|;
specifier|private
name|MessageConsumer
name|tcpConsumer
decl_stmt|;
specifier|private
name|MessageConsumer
name|httpConsumer
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|destinationName
init|=
literal|"HttpCompressionTopic"
decl_stmt|;
annotation|@
name|Test
specifier|public
name|void
name|testTextMessageCompressionFromTcp
parameter_list|()
throws|throws
name|Exception
block|{
name|sendTextMessage
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|doTestTextMessageCompression
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testTextMessageCompressionFromHttp
parameter_list|()
throws|throws
name|Exception
block|{
name|sendTextMessage
argument_list|(
name|httpConnectionFactory
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|doTestTextMessageCompression
argument_list|()
expr_stmt|;
block|}
specifier|private
name|void
name|doTestTextMessageCompression
parameter_list|()
throws|throws
name|Exception
block|{
name|ActiveMQTextMessage
name|tcpMessage
init|=
operator|(
name|ActiveMQTextMessage
operator|)
name|tcpConsumer
operator|.
name|receive
argument_list|(
name|TimeUnit
operator|.
name|SECONDS
operator|.
name|toMillis
argument_list|(
literal|3
argument_list|)
argument_list|)
decl_stmt|;
name|ActiveMQTextMessage
name|httpMessage
init|=
operator|(
name|ActiveMQTextMessage
operator|)
name|httpConsumer
operator|.
name|receive
argument_list|(
name|TimeUnit
operator|.
name|SECONDS
operator|.
name|toMillis
argument_list|(
literal|3
argument_list|)
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|tcpMessage
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|httpMessage
argument_list|)
expr_stmt|;
name|ByteSequence
name|tcpContent
init|=
name|tcpMessage
operator|.
name|getContent
argument_list|()
decl_stmt|;
name|ByteSequence
name|httpContent
init|=
name|httpMessage
operator|.
name|getContent
argument_list|()
decl_stmt|;
name|assertNotNull
argument_list|(
name|tcpContent
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|httpContent
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|tcpMessage
operator|.
name|isCompressed
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|httpMessage
operator|.
name|isCompressed
argument_list|()
argument_list|)
expr_stmt|;
name|int
name|tcpCompressedSize
init|=
name|tcpContent
operator|.
name|getLength
argument_list|()
decl_stmt|;
name|int
name|httpCompressedSize
init|=
name|httpContent
operator|.
name|getLength
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|tcpContent
operator|.
name|getLength
argument_list|()
argument_list|,
name|httpContent
operator|.
name|getLength
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|tcpMessage
operator|.
name|getText
argument_list|()
argument_list|,
name|httpMessage
operator|.
name|getText
argument_list|()
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Received Message on TCP: "
operator|+
name|tcpMessage
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Received Message on HTTP: "
operator|+
name|httpMessage
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|sendTextMessage
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|ActiveMQTextMessage
name|uncompressedHttpMessage
init|=
operator|(
name|ActiveMQTextMessage
operator|)
name|httpConsumer
operator|.
name|receive
argument_list|(
name|TimeUnit
operator|.
name|SECONDS
operator|.
name|toMillis
argument_list|(
literal|3
argument_list|)
argument_list|)
decl_stmt|;
name|int
name|httpUncompressedSize
init|=
name|uncompressedHttpMessage
operator|.
name|getContent
argument_list|()
operator|.
name|getLength
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|httpUncompressedSize
operator|>
name|httpCompressedSize
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|httpUncompressedSize
operator|>
name|tcpCompressedSize
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testBytesMessageCompressionFromTcp
parameter_list|()
throws|throws
name|Exception
block|{
name|sendBytesMessage
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|doTestBytesMessageCompression
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testBytesMessageCompressionFromHttp
parameter_list|()
throws|throws
name|Exception
block|{
name|sendBytesMessage
argument_list|(
name|httpConnectionFactory
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|doTestBytesMessageCompression
argument_list|()
expr_stmt|;
block|}
specifier|private
name|void
name|doTestBytesMessageCompression
parameter_list|()
throws|throws
name|Exception
block|{
name|ActiveMQBytesMessage
name|tcpMessage
init|=
operator|(
name|ActiveMQBytesMessage
operator|)
name|tcpConsumer
operator|.
name|receive
argument_list|(
name|TimeUnit
operator|.
name|SECONDS
operator|.
name|toMillis
argument_list|(
literal|3
argument_list|)
argument_list|)
decl_stmt|;
name|ActiveMQBytesMessage
name|httpMessage
init|=
operator|(
name|ActiveMQBytesMessage
operator|)
name|httpConsumer
operator|.
name|receive
argument_list|(
name|TimeUnit
operator|.
name|SECONDS
operator|.
name|toMillis
argument_list|(
literal|3
argument_list|)
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|tcpMessage
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|httpMessage
argument_list|)
expr_stmt|;
name|ByteSequence
name|tcpContent
init|=
name|tcpMessage
operator|.
name|getContent
argument_list|()
decl_stmt|;
name|ByteSequence
name|httpContent
init|=
name|httpMessage
operator|.
name|getContent
argument_list|()
decl_stmt|;
name|assertNotNull
argument_list|(
name|tcpContent
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|httpContent
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|tcpMessage
operator|.
name|isCompressed
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|httpMessage
operator|.
name|isCompressed
argument_list|()
argument_list|)
expr_stmt|;
name|int
name|tcpCompressedSize
init|=
name|tcpContent
operator|.
name|getLength
argument_list|()
decl_stmt|;
name|int
name|httpCompressedSize
init|=
name|httpContent
operator|.
name|getLength
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|tcpContent
operator|.
name|getLength
argument_list|()
argument_list|,
name|httpContent
operator|.
name|getLength
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|tcpMessage
operator|.
name|readUTF
argument_list|()
argument_list|,
name|httpMessage
operator|.
name|readUTF
argument_list|()
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Received Message on TCP: "
operator|+
name|tcpMessage
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Received Message on HTTP: "
operator|+
name|httpMessage
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|sendBytesMessage
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|ActiveMQBytesMessage
name|uncompressedHttpMessage
init|=
operator|(
name|ActiveMQBytesMessage
operator|)
name|httpConsumer
operator|.
name|receive
argument_list|(
name|TimeUnit
operator|.
name|SECONDS
operator|.
name|toMillis
argument_list|(
literal|3
argument_list|)
argument_list|)
decl_stmt|;
name|int
name|httpUncompressedSize
init|=
name|uncompressedHttpMessage
operator|.
name|getContent
argument_list|()
operator|.
name|getLength
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|httpUncompressedSize
operator|>
name|httpCompressedSize
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|httpUncompressedSize
operator|>
name|tcpCompressedSize
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testStreamMessageCompressionFromTcp
parameter_list|()
throws|throws
name|Exception
block|{
name|sendStreamMessage
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|doTestStreamMessageCompression
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testStreamMessageCompressionFromHttp
parameter_list|()
throws|throws
name|Exception
block|{
name|sendStreamMessage
argument_list|(
name|httpConnectionFactory
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|doTestStreamMessageCompression
argument_list|()
expr_stmt|;
block|}
specifier|private
name|void
name|doTestStreamMessageCompression
parameter_list|()
throws|throws
name|Exception
block|{
name|ActiveMQStreamMessage
name|tcpMessage
init|=
operator|(
name|ActiveMQStreamMessage
operator|)
name|tcpConsumer
operator|.
name|receive
argument_list|(
name|TimeUnit
operator|.
name|SECONDS
operator|.
name|toMillis
argument_list|(
literal|3
argument_list|)
argument_list|)
decl_stmt|;
name|ActiveMQStreamMessage
name|httpMessage
init|=
operator|(
name|ActiveMQStreamMessage
operator|)
name|httpConsumer
operator|.
name|receive
argument_list|(
name|TimeUnit
operator|.
name|SECONDS
operator|.
name|toMillis
argument_list|(
literal|3
argument_list|)
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|tcpMessage
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|httpMessage
argument_list|)
expr_stmt|;
name|ByteSequence
name|tcpContent
init|=
name|tcpMessage
operator|.
name|getContent
argument_list|()
decl_stmt|;
name|ByteSequence
name|httpContent
init|=
name|httpMessage
operator|.
name|getContent
argument_list|()
decl_stmt|;
name|assertNotNull
argument_list|(
name|tcpContent
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|httpContent
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|tcpMessage
operator|.
name|isCompressed
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|httpMessage
operator|.
name|isCompressed
argument_list|()
argument_list|)
expr_stmt|;
name|int
name|tcpCompressedSize
init|=
name|tcpContent
operator|.
name|getLength
argument_list|()
decl_stmt|;
name|int
name|httpCompressedSize
init|=
name|httpContent
operator|.
name|getLength
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|tcpContent
operator|.
name|getLength
argument_list|()
argument_list|,
name|httpContent
operator|.
name|getLength
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|tcpMessage
operator|.
name|readString
argument_list|()
argument_list|,
name|httpMessage
operator|.
name|readString
argument_list|()
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Received Message on TCP: "
operator|+
name|tcpMessage
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Received Message on HTTP: "
operator|+
name|httpMessage
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|sendStreamMessage
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|ActiveMQStreamMessage
name|uncompressedHttpMessage
init|=
operator|(
name|ActiveMQStreamMessage
operator|)
name|httpConsumer
operator|.
name|receive
argument_list|(
name|TimeUnit
operator|.
name|SECONDS
operator|.
name|toMillis
argument_list|(
literal|3
argument_list|)
argument_list|)
decl_stmt|;
name|int
name|httpUncompressedSize
init|=
name|uncompressedHttpMessage
operator|.
name|getContent
argument_list|()
operator|.
name|getLength
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|httpUncompressedSize
operator|>
name|httpCompressedSize
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|httpUncompressedSize
operator|>
name|tcpCompressedSize
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testMapMessageCompressionFromTcp
parameter_list|()
throws|throws
name|Exception
block|{
name|sendMapMessage
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|doTestMapMessageCompression
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testMapMessageCompressionFromHttp
parameter_list|()
throws|throws
name|Exception
block|{
name|sendMapMessage
argument_list|(
name|httpConnectionFactory
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|doTestMapMessageCompression
argument_list|()
expr_stmt|;
block|}
specifier|private
name|void
name|doTestMapMessageCompression
parameter_list|()
throws|throws
name|Exception
block|{
name|ActiveMQMapMessage
name|tcpMessage
init|=
operator|(
name|ActiveMQMapMessage
operator|)
name|tcpConsumer
operator|.
name|receive
argument_list|(
name|TimeUnit
operator|.
name|SECONDS
operator|.
name|toMillis
argument_list|(
literal|3
argument_list|)
argument_list|)
decl_stmt|;
name|ActiveMQMapMessage
name|httpMessage
init|=
operator|(
name|ActiveMQMapMessage
operator|)
name|httpConsumer
operator|.
name|receive
argument_list|(
name|TimeUnit
operator|.
name|SECONDS
operator|.
name|toMillis
argument_list|(
literal|3
argument_list|)
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|tcpMessage
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|httpMessage
argument_list|)
expr_stmt|;
name|ByteSequence
name|tcpContent
init|=
name|tcpMessage
operator|.
name|getContent
argument_list|()
decl_stmt|;
name|ByteSequence
name|httpContent
init|=
name|httpMessage
operator|.
name|getContent
argument_list|()
decl_stmt|;
name|assertNotNull
argument_list|(
name|tcpContent
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|httpContent
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|tcpMessage
operator|.
name|isCompressed
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|httpMessage
operator|.
name|isCompressed
argument_list|()
argument_list|)
expr_stmt|;
name|int
name|tcpCompressedSize
init|=
name|tcpContent
operator|.
name|getLength
argument_list|()
decl_stmt|;
name|int
name|httpCompressedSize
init|=
name|httpContent
operator|.
name|getLength
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|tcpContent
operator|.
name|getLength
argument_list|()
argument_list|,
name|httpContent
operator|.
name|getLength
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|tcpMessage
operator|.
name|getString
argument_list|(
literal|"content"
argument_list|)
argument_list|,
name|httpMessage
operator|.
name|getString
argument_list|(
literal|"content"
argument_list|)
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Received Message on TCP: "
operator|+
name|tcpMessage
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Received Message on HTTP: "
operator|+
name|httpMessage
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|sendMapMessage
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|ActiveMQMapMessage
name|uncompressedHttpMessage
init|=
operator|(
name|ActiveMQMapMessage
operator|)
name|httpConsumer
operator|.
name|receive
argument_list|(
name|TimeUnit
operator|.
name|SECONDS
operator|.
name|toMillis
argument_list|(
literal|3
argument_list|)
argument_list|)
decl_stmt|;
name|int
name|httpUncompressedSize
init|=
name|uncompressedHttpMessage
operator|.
name|getContent
argument_list|()
operator|.
name|getLength
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|httpUncompressedSize
operator|>
name|httpCompressedSize
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|httpUncompressedSize
operator|>
name|tcpCompressedSize
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Before
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|broker
operator|=
operator|new
name|BrokerService
argument_list|()
expr_stmt|;
name|broker
operator|.
name|setPersistent
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|broker
operator|.
name|setUseJmx
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|broker
operator|.
name|setAdvisorySupport
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|broker
operator|.
name|setDeleteAllMessagesOnStartup
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|TransportConnector
name|tcpConnector
init|=
name|broker
operator|.
name|addConnector
argument_list|(
name|tcpBindAddress
argument_list|)
decl_stmt|;
name|TransportConnector
name|httpConnector
init|=
name|broker
operator|.
name|addConnector
argument_list|(
name|httpBindAddress
argument_list|)
decl_stmt|;
name|broker
operator|.
name|start
argument_list|()
expr_stmt|;
name|broker
operator|.
name|waitUntilStarted
argument_list|()
expr_stmt|;
name|WaitForJettyListener
operator|.
name|waitForJettySocketToAccept
argument_list|(
name|httpConnector
operator|.
name|getPublishableConnectString
argument_list|()
argument_list|)
expr_stmt|;
name|tcpConnectionFactory
operator|=
operator|new
name|ActiveMQConnectionFactory
argument_list|(
name|tcpConnector
operator|.
name|getPublishableConnectString
argument_list|()
argument_list|)
expr_stmt|;
name|tcpConnectionFactory
operator|.
name|setUseCompression
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|httpConnectionFactory
operator|=
operator|new
name|ActiveMQConnectionFactory
argument_list|(
name|httpConnector
operator|.
name|getPublishableConnectString
argument_list|()
argument_list|)
expr_stmt|;
name|httpConnectionFactory
operator|.
name|setUseCompression
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|tcpConnection
operator|=
operator|(
name|ActiveMQConnection
operator|)
name|tcpConnectionFactory
operator|.
name|createConnection
argument_list|()
expr_stmt|;
name|httpConnection
operator|=
operator|(
name|ActiveMQConnection
operator|)
name|httpConnectionFactory
operator|.
name|createConnection
argument_list|()
expr_stmt|;
name|tcpSession
operator|=
name|tcpConnection
operator|.
name|createSession
argument_list|(
literal|false
argument_list|,
name|Session
operator|.
name|AUTO_ACKNOWLEDGE
argument_list|)
expr_stmt|;
name|httpSession
operator|=
name|httpConnection
operator|.
name|createSession
argument_list|(
literal|false
argument_list|,
name|Session
operator|.
name|AUTO_ACKNOWLEDGE
argument_list|)
expr_stmt|;
name|destination
operator|=
name|tcpSession
operator|.
name|createTopic
argument_list|(
name|destinationName
argument_list|)
expr_stmt|;
name|tcpConsumer
operator|=
name|tcpSession
operator|.
name|createConsumer
argument_list|(
name|destination
argument_list|)
expr_stmt|;
name|httpConsumer
operator|=
name|httpSession
operator|.
name|createConsumer
argument_list|(
name|destination
argument_list|)
expr_stmt|;
name|tcpConnection
operator|.
name|start
argument_list|()
expr_stmt|;
name|httpConnection
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
annotation|@
name|After
specifier|public
name|void
name|shutDown
parameter_list|()
throws|throws
name|Exception
block|{
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
name|broker
operator|.
name|waitUntilStopped
argument_list|()
expr_stmt|;
block|}
block|}
specifier|private
name|void
name|sendTextMessage
parameter_list|(
name|boolean
name|compressed
parameter_list|)
throws|throws
name|Exception
block|{
name|sendTextMessage
argument_list|(
name|tcpConnectionFactory
argument_list|,
name|compressed
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|sendTextMessage
parameter_list|(
name|ActiveMQConnectionFactory
name|factory
parameter_list|,
name|boolean
name|compressed
parameter_list|)
throws|throws
name|Exception
block|{
name|StringBuilder
name|builder
init|=
operator|new
name|StringBuilder
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
literal|10
condition|;
operator|++
name|i
control|)
block|{
name|builder
operator|.
name|append
argument_list|(
name|UUID
operator|.
name|randomUUID
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|ActiveMQConnection
name|connection
init|=
operator|(
name|ActiveMQConnection
operator|)
name|factory
operator|.
name|createConnection
argument_list|()
decl_stmt|;
name|connection
operator|.
name|setUseCompression
argument_list|(
name|compressed
argument_list|)
expr_stmt|;
name|Session
name|session
init|=
name|connection
operator|.
name|createSession
argument_list|(
literal|false
argument_list|,
name|Session
operator|.
name|AUTO_ACKNOWLEDGE
argument_list|)
decl_stmt|;
name|Topic
name|destination
init|=
name|session
operator|.
name|createTopic
argument_list|(
name|destinationName
argument_list|)
decl_stmt|;
name|MessageProducer
name|producer
init|=
name|session
operator|.
name|createProducer
argument_list|(
name|destination
argument_list|)
decl_stmt|;
name|producer
operator|.
name|send
argument_list|(
name|session
operator|.
name|createTextMessage
argument_list|(
name|builder
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|sendBytesMessage
parameter_list|(
name|boolean
name|compressed
parameter_list|)
throws|throws
name|Exception
block|{
name|sendBytesMessage
argument_list|(
name|tcpConnectionFactory
argument_list|,
name|compressed
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|sendBytesMessage
parameter_list|(
name|ActiveMQConnectionFactory
name|factory
parameter_list|,
name|boolean
name|compressed
parameter_list|)
throws|throws
name|Exception
block|{
name|StringBuilder
name|builder
init|=
operator|new
name|StringBuilder
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
literal|10
condition|;
operator|++
name|i
control|)
block|{
name|builder
operator|.
name|append
argument_list|(
name|UUID
operator|.
name|randomUUID
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|ActiveMQConnection
name|connection
init|=
operator|(
name|ActiveMQConnection
operator|)
name|factory
operator|.
name|createConnection
argument_list|()
decl_stmt|;
name|connection
operator|.
name|setUseCompression
argument_list|(
name|compressed
argument_list|)
expr_stmt|;
name|Session
name|session
init|=
name|connection
operator|.
name|createSession
argument_list|(
literal|false
argument_list|,
name|Session
operator|.
name|AUTO_ACKNOWLEDGE
argument_list|)
decl_stmt|;
name|Topic
name|destination
init|=
name|session
operator|.
name|createTopic
argument_list|(
name|destinationName
argument_list|)
decl_stmt|;
name|MessageProducer
name|producer
init|=
name|session
operator|.
name|createProducer
argument_list|(
name|destination
argument_list|)
decl_stmt|;
name|BytesMessage
name|message
init|=
name|session
operator|.
name|createBytesMessage
argument_list|()
decl_stmt|;
name|message
operator|.
name|writeUTF
argument_list|(
name|builder
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|producer
operator|.
name|send
argument_list|(
name|message
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|sendStreamMessage
parameter_list|(
name|boolean
name|compressed
parameter_list|)
throws|throws
name|Exception
block|{
name|sendStreamMessage
argument_list|(
name|tcpConnectionFactory
argument_list|,
name|compressed
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|sendStreamMessage
parameter_list|(
name|ActiveMQConnectionFactory
name|factory
parameter_list|,
name|boolean
name|compressed
parameter_list|)
throws|throws
name|Exception
block|{
name|StringBuilder
name|builder
init|=
operator|new
name|StringBuilder
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
literal|10
condition|;
operator|++
name|i
control|)
block|{
name|builder
operator|.
name|append
argument_list|(
name|UUID
operator|.
name|randomUUID
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|ActiveMQConnection
name|connection
init|=
operator|(
name|ActiveMQConnection
operator|)
name|factory
operator|.
name|createConnection
argument_list|()
decl_stmt|;
name|connection
operator|.
name|setUseCompression
argument_list|(
name|compressed
argument_list|)
expr_stmt|;
name|Session
name|session
init|=
name|connection
operator|.
name|createSession
argument_list|(
literal|false
argument_list|,
name|Session
operator|.
name|AUTO_ACKNOWLEDGE
argument_list|)
decl_stmt|;
name|Topic
name|destination
init|=
name|session
operator|.
name|createTopic
argument_list|(
name|destinationName
argument_list|)
decl_stmt|;
name|MessageProducer
name|producer
init|=
name|session
operator|.
name|createProducer
argument_list|(
name|destination
argument_list|)
decl_stmt|;
name|StreamMessage
name|message
init|=
name|session
operator|.
name|createStreamMessage
argument_list|()
decl_stmt|;
name|message
operator|.
name|writeString
argument_list|(
name|builder
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|producer
operator|.
name|send
argument_list|(
name|message
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|sendMapMessage
parameter_list|(
name|boolean
name|compressed
parameter_list|)
throws|throws
name|Exception
block|{
name|sendMapMessage
argument_list|(
name|tcpConnectionFactory
argument_list|,
name|compressed
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|sendMapMessage
parameter_list|(
name|ActiveMQConnectionFactory
name|factory
parameter_list|,
name|boolean
name|compressed
parameter_list|)
throws|throws
name|Exception
block|{
name|StringBuilder
name|builder
init|=
operator|new
name|StringBuilder
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
literal|10
condition|;
operator|++
name|i
control|)
block|{
name|builder
operator|.
name|append
argument_list|(
name|UUID
operator|.
name|randomUUID
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|ActiveMQConnection
name|connection
init|=
operator|(
name|ActiveMQConnection
operator|)
name|factory
operator|.
name|createConnection
argument_list|()
decl_stmt|;
name|connection
operator|.
name|setUseCompression
argument_list|(
name|compressed
argument_list|)
expr_stmt|;
name|Session
name|session
init|=
name|connection
operator|.
name|createSession
argument_list|(
literal|false
argument_list|,
name|Session
operator|.
name|AUTO_ACKNOWLEDGE
argument_list|)
decl_stmt|;
name|Topic
name|destination
init|=
name|session
operator|.
name|createTopic
argument_list|(
name|destinationName
argument_list|)
decl_stmt|;
name|MessageProducer
name|producer
init|=
name|session
operator|.
name|createProducer
argument_list|(
name|destination
argument_list|)
decl_stmt|;
name|MapMessage
name|message
init|=
name|session
operator|.
name|createMapMessage
argument_list|()
decl_stmt|;
name|message
operator|.
name|setString
argument_list|(
literal|"content"
argument_list|,
name|builder
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|producer
operator|.
name|send
argument_list|(
name|message
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

