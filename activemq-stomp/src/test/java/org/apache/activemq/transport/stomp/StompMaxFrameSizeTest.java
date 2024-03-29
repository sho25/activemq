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
name|stomp
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
name|assertEquals
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
name|assertNotNull
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

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|Socket
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Arrays
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collection
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|net
operator|.
name|SocketFactory
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|net
operator|.
name|ssl
operator|.
name|SSLSocketFactory
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Assume
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
name|junit
operator|.
name|runner
operator|.
name|RunWith
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|runners
operator|.
name|Parameterized
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|runners
operator|.
name|Parameterized
operator|.
name|Parameters
import|;
end_import

begin_class
annotation|@
name|RunWith
argument_list|(
name|Parameterized
operator|.
name|class
argument_list|)
specifier|public
class|class
name|StompMaxFrameSizeTest
extends|extends
name|StompTestSupport
block|{
enum|enum
name|TestType
block|{
name|FRAME_MAX_GREATER_THAN_HEADER_MAX
block|,
name|FRAME_MAX_LESS_THAN_HEADER_MAX
block|,
name|FRAME_MAX_LESS_THAN_ACTION_MAX
block|}
empty_stmt|;
comment|// set max data size higher than max frame size so that max frame size gets tested
specifier|private
specifier|static
specifier|final
name|int
name|MAX_DATA_SIZE
init|=
literal|100
operator|*
literal|1024
decl_stmt|;
specifier|private
specifier|final
name|TestType
name|testType
decl_stmt|;
specifier|private
specifier|final
name|int
name|maxFrameSize
decl_stmt|;
comment|/**      * This defines the different possible max header sizes for this test.      */
annotation|@
name|Parameters
specifier|public
specifier|static
name|Collection
argument_list|<
name|Object
index|[]
argument_list|>
name|data
parameter_list|()
block|{
return|return
name|Arrays
operator|.
name|asList
argument_list|(
operator|new
name|Object
index|[]
index|[]
block|{
comment|// The maximum size exceeds the default max header size of 10 * 1024
block|{
name|TestType
operator|.
name|FRAME_MAX_GREATER_THAN_HEADER_MAX
block|,
literal|64
operator|*
literal|1024
block|}
block|,
comment|// The maximum size is less than the default max header size of 10 * 1024
block|{
name|TestType
operator|.
name|FRAME_MAX_LESS_THAN_HEADER_MAX
block|,
literal|5
operator|*
literal|1024
block|}
block|,
comment|// The maximum size is less than the default max action size of 1024
block|{
name|TestType
operator|.
name|FRAME_MAX_LESS_THAN_ACTION_MAX
block|,
literal|512
block|}
block|}
argument_list|)
return|;
block|}
specifier|public
name|StompMaxFrameSizeTest
parameter_list|(
name|TestType
name|testType
parameter_list|,
name|int
name|maxFrameSize
parameter_list|)
block|{
name|this
operator|.
name|testType
operator|=
name|testType
expr_stmt|;
name|this
operator|.
name|maxFrameSize
operator|=
name|maxFrameSize
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|boolean
name|isUseSslConnector
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
annotation|@
name|Override
specifier|protected
name|boolean
name|isUseNioConnector
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
annotation|@
name|Override
specifier|protected
name|boolean
name|isUseNioPlusSslConnector
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
annotation|@
name|Override
specifier|protected
name|String
name|getAdditionalConfig
parameter_list|()
block|{
return|return
literal|"?wireFormat.maxDataLength="
operator|+
name|MAX_DATA_SIZE
operator|+
literal|"&wireFormat.maxFrameSize="
operator|+
name|maxFrameSize
return|;
block|}
comment|/**      * These tests should cause a Stomp error because the body size is greater than the      * max allowed frame size      */
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|60000
argument_list|)
specifier|public
name|void
name|testOversizedBodyOnPlainSocket
parameter_list|()
throws|throws
name|Exception
block|{
name|Assume
operator|.
name|assumeTrue
argument_list|(
name|testType
operator|==
name|TestType
operator|.
name|FRAME_MAX_GREATER_THAN_HEADER_MAX
argument_list|)
expr_stmt|;
name|doOversizedTestMessage
argument_list|(
name|port
argument_list|,
literal|false
argument_list|,
name|maxFrameSize
operator|+
literal|100
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|60000
argument_list|)
specifier|public
name|void
name|testOversizedBodyOnNioSocket
parameter_list|()
throws|throws
name|Exception
block|{
name|Assume
operator|.
name|assumeTrue
argument_list|(
name|testType
operator|==
name|TestType
operator|.
name|FRAME_MAX_GREATER_THAN_HEADER_MAX
argument_list|)
expr_stmt|;
name|doOversizedTestMessage
argument_list|(
name|nioPort
argument_list|,
literal|false
argument_list|,
name|maxFrameSize
operator|+
literal|100
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|60000
argument_list|)
specifier|public
name|void
name|testOversizedBodyOnSslSocket
parameter_list|()
throws|throws
name|Exception
block|{
name|Assume
operator|.
name|assumeTrue
argument_list|(
name|testType
operator|==
name|TestType
operator|.
name|FRAME_MAX_GREATER_THAN_HEADER_MAX
argument_list|)
expr_stmt|;
name|doOversizedTestMessage
argument_list|(
name|sslPort
argument_list|,
literal|true
argument_list|,
name|maxFrameSize
operator|+
literal|100
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|60000
argument_list|)
specifier|public
name|void
name|testOversizedBodyOnNioSslSocket
parameter_list|()
throws|throws
name|Exception
block|{
name|Assume
operator|.
name|assumeTrue
argument_list|(
name|testType
operator|==
name|TestType
operator|.
name|FRAME_MAX_GREATER_THAN_HEADER_MAX
argument_list|)
expr_stmt|;
name|doOversizedTestMessage
argument_list|(
name|nioSslPort
argument_list|,
literal|true
argument_list|,
name|maxFrameSize
operator|+
literal|100
argument_list|)
expr_stmt|;
block|}
comment|/**      * These tests should cause a Stomp error because even though the body size is less than max frame size,      * the action and headers plus data size should cause a max frame size failure      */
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|60000
argument_list|)
specifier|public
name|void
name|testOversizedTotalFrameOnPlainSocket
parameter_list|()
throws|throws
name|Exception
block|{
name|Assume
operator|.
name|assumeTrue
argument_list|(
name|testType
operator|==
name|TestType
operator|.
name|FRAME_MAX_GREATER_THAN_HEADER_MAX
argument_list|)
expr_stmt|;
name|doOversizedTestMessage
argument_list|(
name|port
argument_list|,
literal|false
argument_list|,
name|maxFrameSize
operator|-
literal|50
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|60000
argument_list|)
specifier|public
name|void
name|testOversizedTotalFrameOnNioSocket
parameter_list|()
throws|throws
name|Exception
block|{
name|Assume
operator|.
name|assumeTrue
argument_list|(
name|testType
operator|==
name|TestType
operator|.
name|FRAME_MAX_GREATER_THAN_HEADER_MAX
argument_list|)
expr_stmt|;
name|doOversizedTestMessage
argument_list|(
name|nioPort
argument_list|,
literal|false
argument_list|,
name|maxFrameSize
operator|-
literal|50
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|60000
argument_list|)
specifier|public
name|void
name|testOversizedTotalFrameOnSslSocket
parameter_list|()
throws|throws
name|Exception
block|{
name|Assume
operator|.
name|assumeTrue
argument_list|(
name|testType
operator|==
name|TestType
operator|.
name|FRAME_MAX_GREATER_THAN_HEADER_MAX
argument_list|)
expr_stmt|;
name|doOversizedTestMessage
argument_list|(
name|sslPort
argument_list|,
literal|true
argument_list|,
name|maxFrameSize
operator|-
literal|50
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|60000
argument_list|)
specifier|public
name|void
name|testOversizedTotalFrameOnNioSslSocket
parameter_list|()
throws|throws
name|Exception
block|{
name|Assume
operator|.
name|assumeTrue
argument_list|(
name|testType
operator|==
name|TestType
operator|.
name|FRAME_MAX_GREATER_THAN_HEADER_MAX
argument_list|)
expr_stmt|;
name|doOversizedTestMessage
argument_list|(
name|nioSslPort
argument_list|,
literal|true
argument_list|,
name|maxFrameSize
operator|-
literal|50
argument_list|)
expr_stmt|;
block|}
comment|/**      * These tests will test a successful Stomp message when the total size is than max frame size      */
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|60000
argument_list|)
specifier|public
name|void
name|testUndersizedTotalFrameOnPlainSocket
parameter_list|()
throws|throws
name|Exception
block|{
name|Assume
operator|.
name|assumeTrue
argument_list|(
name|testType
operator|==
name|TestType
operator|.
name|FRAME_MAX_GREATER_THAN_HEADER_MAX
argument_list|)
expr_stmt|;
name|doUndersizedTestMessage
argument_list|(
name|port
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|60000
argument_list|)
specifier|public
name|void
name|testUndersizedTotalFrameOnNioSocket
parameter_list|()
throws|throws
name|Exception
block|{
name|Assume
operator|.
name|assumeTrue
argument_list|(
name|testType
operator|==
name|TestType
operator|.
name|FRAME_MAX_GREATER_THAN_HEADER_MAX
argument_list|)
expr_stmt|;
name|doUndersizedTestMessage
argument_list|(
name|nioPort
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|60000
argument_list|)
specifier|public
name|void
name|testUndersizedTotalFrameOnSslSocket
parameter_list|()
throws|throws
name|Exception
block|{
name|Assume
operator|.
name|assumeTrue
argument_list|(
name|testType
operator|==
name|TestType
operator|.
name|FRAME_MAX_GREATER_THAN_HEADER_MAX
argument_list|)
expr_stmt|;
name|doUndersizedTestMessage
argument_list|(
name|sslPort
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|60000
argument_list|)
specifier|public
name|void
name|testUndersizedTotalFrameOnNioSslSocket
parameter_list|()
throws|throws
name|Exception
block|{
name|Assume
operator|.
name|assumeTrue
argument_list|(
name|testType
operator|==
name|TestType
operator|.
name|FRAME_MAX_GREATER_THAN_HEADER_MAX
argument_list|)
expr_stmt|;
name|doUndersizedTestMessage
argument_list|(
name|nioSslPort
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
comment|/**      *  These tests test that a Stomp error occurs if the action size exceeds maxFrameSize      *  when the maxFrameSize length is less than the default max action length      */
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|60000
argument_list|)
specifier|public
name|void
name|testOversizedActionOnPlainSocket
parameter_list|()
throws|throws
name|Exception
block|{
name|Assume
operator|.
name|assumeTrue
argument_list|(
name|testType
operator|==
name|TestType
operator|.
name|FRAME_MAX_LESS_THAN_ACTION_MAX
argument_list|)
expr_stmt|;
name|doTestOversizedAction
argument_list|(
name|port
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|60000
argument_list|)
specifier|public
name|void
name|testOversizedActionOnNioSocket
parameter_list|()
throws|throws
name|Exception
block|{
name|Assume
operator|.
name|assumeTrue
argument_list|(
name|testType
operator|==
name|TestType
operator|.
name|FRAME_MAX_LESS_THAN_ACTION_MAX
argument_list|)
expr_stmt|;
name|doTestOversizedAction
argument_list|(
name|nioPort
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|60000
argument_list|)
specifier|public
name|void
name|testOversizedActionOnSslSocket
parameter_list|()
throws|throws
name|Exception
block|{
name|Assume
operator|.
name|assumeTrue
argument_list|(
name|testType
operator|==
name|TestType
operator|.
name|FRAME_MAX_LESS_THAN_ACTION_MAX
argument_list|)
expr_stmt|;
name|doTestOversizedAction
argument_list|(
name|sslPort
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|60000
argument_list|)
specifier|public
name|void
name|testOversizedActionOnNioSslSocket
parameter_list|()
throws|throws
name|Exception
block|{
name|Assume
operator|.
name|assumeTrue
argument_list|(
name|testType
operator|==
name|TestType
operator|.
name|FRAME_MAX_LESS_THAN_ACTION_MAX
argument_list|)
expr_stmt|;
name|doTestOversizedAction
argument_list|(
name|nioSslPort
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
comment|/**      *  These tests will test that a Stomp error occurs if the header size exceeds maxFrameSize      *  when the maxFrameSize length is less than the default max header length      */
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|60000
argument_list|)
specifier|public
name|void
name|testOversizedHeadersOnPlainSocket
parameter_list|()
throws|throws
name|Exception
block|{
name|Assume
operator|.
name|assumeTrue
argument_list|(
name|testType
operator|==
name|TestType
operator|.
name|FRAME_MAX_LESS_THAN_HEADER_MAX
argument_list|)
expr_stmt|;
name|doTestOversizedHeaders
argument_list|(
name|port
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|60000
argument_list|)
specifier|public
name|void
name|testOversizedHeadersOnNioSocket
parameter_list|()
throws|throws
name|Exception
block|{
name|Assume
operator|.
name|assumeTrue
argument_list|(
name|testType
operator|==
name|TestType
operator|.
name|FRAME_MAX_LESS_THAN_HEADER_MAX
argument_list|)
expr_stmt|;
name|doTestOversizedHeaders
argument_list|(
name|nioPort
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|60000
argument_list|)
specifier|public
name|void
name|testOversizedHeadersOnSslSocket
parameter_list|()
throws|throws
name|Exception
block|{
name|Assume
operator|.
name|assumeTrue
argument_list|(
name|testType
operator|==
name|TestType
operator|.
name|FRAME_MAX_LESS_THAN_HEADER_MAX
argument_list|)
expr_stmt|;
name|doTestOversizedHeaders
argument_list|(
name|sslPort
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|60000
argument_list|)
specifier|public
name|void
name|testOversizedHeadersOnNioSslSocket
parameter_list|()
throws|throws
name|Exception
block|{
name|Assume
operator|.
name|assumeTrue
argument_list|(
name|testType
operator|==
name|TestType
operator|.
name|FRAME_MAX_LESS_THAN_HEADER_MAX
argument_list|)
expr_stmt|;
name|doTestOversizedHeaders
argument_list|(
name|nioSslPort
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|void
name|doTestOversizedAction
parameter_list|(
name|int
name|port
parameter_list|,
name|boolean
name|useSsl
parameter_list|)
throws|throws
name|Exception
block|{
name|initializeStomp
argument_list|(
name|port
argument_list|,
name|useSsl
argument_list|)
expr_stmt|;
name|char
index|[]
name|actionArray
init|=
operator|new
name|char
index|[
name|maxFrameSize
operator|+
literal|100
index|]
decl_stmt|;
name|Arrays
operator|.
name|fill
argument_list|(
name|actionArray
argument_list|,
literal|'A'
argument_list|)
expr_stmt|;
name|String
name|action
init|=
operator|new
name|String
argument_list|(
name|actionArray
argument_list|)
decl_stmt|;
name|String
name|frame
init|=
name|action
operator|+
literal|"\n"
operator|+
literal|"destination:/queue/"
operator|+
name|getQueueName
argument_list|()
operator|+
literal|"\n\n"
operator|+
literal|"body"
operator|+
name|Stomp
operator|.
name|NULL
decl_stmt|;
name|stompConnection
operator|.
name|sendFrame
argument_list|(
name|frame
argument_list|)
expr_stmt|;
name|StompFrame
name|received
init|=
name|stompConnection
operator|.
name|receive
argument_list|(
literal|500000
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|received
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"ERROR"
argument_list|,
name|received
operator|.
name|getAction
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|received
operator|.
name|getBody
argument_list|()
operator|.
name|contains
argument_list|(
literal|"maximum frame size"
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|void
name|doTestOversizedHeaders
parameter_list|(
name|int
name|port
parameter_list|,
name|boolean
name|useSsl
parameter_list|)
throws|throws
name|Exception
block|{
name|initializeStomp
argument_list|(
name|port
argument_list|,
name|useSsl
argument_list|)
expr_stmt|;
name|StringBuilder
name|headers
init|=
operator|new
name|StringBuilder
argument_list|(
name|maxFrameSize
operator|+
literal|100
argument_list|)
decl_stmt|;
name|int
name|i
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|headers
operator|.
name|length
argument_list|()
operator|<
name|maxFrameSize
operator|+
literal|1
condition|)
block|{
name|headers
operator|.
name|append
argument_list|(
literal|"key"
operator|+
name|i
operator|++
operator|+
literal|":value\n"
argument_list|)
expr_stmt|;
block|}
name|String
name|frame
init|=
literal|"SEND\n"
operator|+
name|headers
operator|.
name|toString
argument_list|()
operator|+
literal|"\n"
operator|+
literal|"destination:/queue/"
operator|+
name|getQueueName
argument_list|()
operator|+
name|headers
operator|.
name|toString
argument_list|()
operator|+
literal|"\n\n"
operator|+
literal|"body"
operator|+
name|Stomp
operator|.
name|NULL
decl_stmt|;
name|stompConnection
operator|.
name|sendFrame
argument_list|(
name|frame
argument_list|)
expr_stmt|;
name|StompFrame
name|received
init|=
name|stompConnection
operator|.
name|receive
argument_list|(
literal|5000
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|received
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"ERROR"
argument_list|,
name|received
operator|.
name|getAction
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|received
operator|.
name|getBody
argument_list|()
operator|.
name|contains
argument_list|(
literal|"maximum frame size"
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|void
name|doOversizedTestMessage
parameter_list|(
name|int
name|port
parameter_list|,
name|boolean
name|useSsl
parameter_list|,
name|int
name|dataSize
parameter_list|)
throws|throws
name|Exception
block|{
name|initializeStomp
argument_list|(
name|port
argument_list|,
name|useSsl
argument_list|)
expr_stmt|;
name|int
name|size
init|=
name|dataSize
operator|+
literal|100
decl_stmt|;
name|char
index|[]
name|bigBodyArray
init|=
operator|new
name|char
index|[
name|size
index|]
decl_stmt|;
name|Arrays
operator|.
name|fill
argument_list|(
name|bigBodyArray
argument_list|,
literal|'a'
argument_list|)
expr_stmt|;
name|String
name|bigBody
init|=
operator|new
name|String
argument_list|(
name|bigBodyArray
argument_list|)
decl_stmt|;
name|String
name|frame
init|=
literal|"SEND\n"
operator|+
literal|"destination:/queue/"
operator|+
name|getQueueName
argument_list|()
operator|+
literal|"\n\n"
operator|+
name|bigBody
operator|+
name|Stomp
operator|.
name|NULL
decl_stmt|;
name|stompConnection
operator|.
name|sendFrame
argument_list|(
name|frame
argument_list|)
expr_stmt|;
name|StompFrame
name|received
init|=
name|stompConnection
operator|.
name|receive
argument_list|(
literal|5000
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|received
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"ERROR"
argument_list|,
name|received
operator|.
name|getAction
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|received
operator|.
name|getBody
argument_list|()
operator|.
name|contains
argument_list|(
literal|"maximum frame size"
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|void
name|doUndersizedTestMessage
parameter_list|(
name|int
name|port
parameter_list|,
name|boolean
name|useSsl
parameter_list|)
throws|throws
name|Exception
block|{
name|initializeStomp
argument_list|(
name|port
argument_list|,
name|useSsl
argument_list|)
expr_stmt|;
name|int
name|size
init|=
literal|100
decl_stmt|;
name|char
index|[]
name|bigBodyArray
init|=
operator|new
name|char
index|[
name|size
index|]
decl_stmt|;
name|Arrays
operator|.
name|fill
argument_list|(
name|bigBodyArray
argument_list|,
literal|'a'
argument_list|)
expr_stmt|;
name|String
name|bigBody
init|=
operator|new
name|String
argument_list|(
name|bigBodyArray
argument_list|)
decl_stmt|;
name|String
name|frame
init|=
literal|"SEND\n"
operator|+
literal|"destination:/queue/"
operator|+
name|getQueueName
argument_list|()
operator|+
literal|"\n\n"
operator|+
name|bigBody
operator|+
name|Stomp
operator|.
name|NULL
decl_stmt|;
name|stompConnection
operator|.
name|sendFrame
argument_list|(
name|frame
argument_list|)
expr_stmt|;
name|StompFrame
name|received
init|=
name|stompConnection
operator|.
name|receive
argument_list|()
decl_stmt|;
name|assertNotNull
argument_list|(
name|received
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"MESSAGE"
argument_list|,
name|received
operator|.
name|getAction
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|bigBody
argument_list|,
name|received
operator|.
name|getBody
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|StompConnection
name|stompConnect
parameter_list|(
name|int
name|port
parameter_list|,
name|boolean
name|ssl
parameter_list|)
throws|throws
name|Exception
block|{
if|if
condition|(
name|stompConnection
operator|==
literal|null
condition|)
block|{
name|stompConnection
operator|=
operator|new
name|StompConnection
argument_list|()
expr_stmt|;
block|}
name|Socket
name|socket
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|ssl
condition|)
block|{
name|socket
operator|=
name|createSslSocket
argument_list|(
name|port
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|socket
operator|=
name|createSocket
argument_list|(
name|port
argument_list|)
expr_stmt|;
block|}
name|stompConnection
operator|.
name|open
argument_list|(
name|socket
argument_list|)
expr_stmt|;
return|return
name|stompConnection
return|;
block|}
specifier|protected
name|void
name|initializeStomp
parameter_list|(
name|int
name|port
parameter_list|,
name|boolean
name|useSsl
parameter_list|)
throws|throws
name|Exception
block|{
name|stompConnect
argument_list|(
name|port
argument_list|,
name|useSsl
argument_list|)
expr_stmt|;
name|String
name|frame
init|=
literal|"CONNECT\n"
operator|+
literal|"login:system\n"
operator|+
literal|"passcode:manager\n\n"
operator|+
name|Stomp
operator|.
name|NULL
decl_stmt|;
name|stompConnection
operator|.
name|sendFrame
argument_list|(
name|frame
argument_list|)
expr_stmt|;
name|frame
operator|=
name|stompConnection
operator|.
name|receiveFrame
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|frame
operator|.
name|startsWith
argument_list|(
literal|"CONNECTED"
argument_list|)
argument_list|)
expr_stmt|;
name|frame
operator|=
literal|"SUBSCRIBE\n"
operator|+
literal|"destination:/queue/"
operator|+
name|getQueueName
argument_list|()
operator|+
literal|"\n"
operator|+
literal|"ack:auto\n\n"
operator|+
name|Stomp
operator|.
name|NULL
expr_stmt|;
name|stompConnection
operator|.
name|sendFrame
argument_list|(
name|frame
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|Socket
name|createSocket
parameter_list|(
name|int
name|port
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|Socket
argument_list|(
literal|"127.0.0.1"
argument_list|,
name|port
argument_list|)
return|;
block|}
specifier|protected
name|Socket
name|createSslSocket
parameter_list|(
name|int
name|port
parameter_list|)
throws|throws
name|IOException
block|{
name|SocketFactory
name|factory
init|=
name|SSLSocketFactory
operator|.
name|getDefault
argument_list|()
decl_stmt|;
return|return
name|factory
operator|.
name|createSocket
argument_list|(
literal|"127.0.0.1"
argument_list|,
name|port
argument_list|)
return|;
block|}
block|}
end_class

end_unit

