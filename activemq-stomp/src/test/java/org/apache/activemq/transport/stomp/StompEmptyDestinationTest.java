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
name|assertTrue
import|;
end_import

begin_class
specifier|public
class|class
name|StompEmptyDestinationTest
extends|extends
name|StompTestSupport
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
name|StompEmptyDestinationTest
operator|.
name|class
argument_list|)
decl_stmt|;
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|60000
argument_list|)
specifier|public
name|void
name|testEmptyDestinationOnSubscribe
parameter_list|()
throws|throws
name|Exception
block|{
name|stompConnect
argument_list|()
expr_stmt|;
name|stompConnection
operator|.
name|sendFrame
argument_list|(
literal|"CONNECT\n"
operator|+
literal|"login:system\n"
operator|+
literal|"passcode:manager\n\n"
operator|+
name|Stomp
operator|.
name|NULL
argument_list|)
expr_stmt|;
name|StompFrame
name|frame
init|=
name|stompConnection
operator|.
name|receive
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|frame
operator|.
name|toString
argument_list|()
operator|.
name|startsWith
argument_list|(
literal|"CONNECTED"
argument_list|)
argument_list|)
expr_stmt|;
name|String
name|send
init|=
literal|"SUBSCRIBE\r\n"
operator|+
literal|"id:1\r\n"
operator|+
literal|"destination:\r\n"
operator|+
literal|"receipt:1\r\n"
operator|+
literal|"\r\n"
operator|+
literal|"\u0000\r\n"
decl_stmt|;
name|stompConnection
operator|.
name|sendFrame
argument_list|(
name|send
argument_list|)
expr_stmt|;
name|StompFrame
name|receipt
init|=
name|stompConnection
operator|.
name|receive
argument_list|()
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Broker sent: "
operator|+
name|receipt
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|receipt
operator|.
name|getAction
argument_list|()
operator|.
name|startsWith
argument_list|(
literal|"ERROR"
argument_list|)
argument_list|)
expr_stmt|;
name|String
name|errorMessage
init|=
name|receipt
operator|.
name|getHeaders
argument_list|()
operator|.
name|get
argument_list|(
literal|"message"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"Invalid empty or 'null' Destination header"
argument_list|,
name|errorMessage
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

