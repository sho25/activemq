begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Copyright (c) 2005 Your Corporation. All Rights Reserved.  */
end_comment

begin_package
package|package
name|org
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
name|activemq
operator|.
name|command
operator|.
name|ConnectionInfo
import|;
end_import

begin_import
import|import
name|org
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
name|activemq
operator|.
name|command
operator|.
name|SessionInfo
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|ByteArrayInputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|ByteArrayOutputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|DataInputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|DataOutputStream
import|;
end_import

begin_import
import|import
name|junit
operator|.
name|framework
operator|.
name|TestCase
import|;
end_import

begin_class
specifier|public
class|class
name|StompWireFormatTest
extends|extends
name|TestCase
block|{
specifier|private
name|StompWireFormat
name|wire
decl_stmt|;
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|wire
operator|=
operator|new
name|StompWireFormat
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|testDummy
parameter_list|()
throws|throws
name|Exception
block|{     }
specifier|public
name|void
name|TODO_testValidConnectHandshake
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|connect_frame
init|=
literal|"CONNECT\n"
operator|+
literal|"login: brianm\n"
operator|+
literal|"passcode: wombats\n"
operator|+
literal|"\n"
operator|+
name|Stomp
operator|.
name|NULL
decl_stmt|;
name|DataInputStream
name|din
init|=
operator|new
name|DataInputStream
argument_list|(
operator|new
name|ByteArrayInputStream
argument_list|(
name|connect_frame
operator|.
name|getBytes
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|ByteArrayOutputStream
name|bout
init|=
operator|new
name|ByteArrayOutputStream
argument_list|()
decl_stmt|;
name|DataOutputStream
name|dout
init|=
operator|new
name|DataOutputStream
argument_list|(
name|bout
argument_list|)
decl_stmt|;
name|wire
operator|.
name|registerTransportStreams
argument_list|(
name|dout
argument_list|,
name|din
argument_list|)
expr_stmt|;
name|wire
operator|.
name|initiateServerSideProtocol
argument_list|()
expr_stmt|;
name|ConnectionInfo
name|ci
init|=
operator|(
name|ConnectionInfo
operator|)
name|wire
operator|.
name|readCommand
argument_list|(
name|din
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|ci
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|ci
operator|.
name|isResponseRequired
argument_list|()
argument_list|)
expr_stmt|;
name|Response
name|cr
init|=
operator|new
name|Response
argument_list|()
decl_stmt|;
name|cr
operator|.
name|setCorrelationId
argument_list|(
name|ci
operator|.
name|getCommandId
argument_list|()
argument_list|)
expr_stmt|;
name|wire
operator|.
name|writeCommand
argument_list|(
name|cr
argument_list|,
name|dout
argument_list|)
expr_stmt|;
name|SessionInfo
name|si
init|=
operator|(
name|SessionInfo
operator|)
name|wire
operator|.
name|readCommand
argument_list|(
literal|null
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|si
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|si
operator|.
name|isResponseRequired
argument_list|()
argument_list|)
expr_stmt|;
name|Response
name|sr
init|=
operator|new
name|Response
argument_list|()
decl_stmt|;
name|sr
operator|.
name|setCorrelationId
argument_list|(
name|si
operator|.
name|getCommandId
argument_list|()
argument_list|)
expr_stmt|;
name|wire
operator|.
name|writeCommand
argument_list|(
name|sr
argument_list|,
name|dout
argument_list|)
expr_stmt|;
name|String
name|response
init|=
operator|new
name|String
argument_list|(
name|bout
operator|.
name|toByteArray
argument_list|()
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|response
operator|.
name|startsWith
argument_list|(
literal|"CONNECTED"
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|_testFakeServer
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|BrokerService
name|container
init|=
operator|new
name|BrokerService
argument_list|()
decl_stmt|;
operator|new
name|Thread
argument_list|(
operator|new
name|Runnable
argument_list|()
block|{
specifier|public
name|void
name|run
parameter_list|()
block|{
try|try
block|{
name|container
operator|.
name|addConnector
argument_list|(
literal|"stomp://localhost:61613"
argument_list|)
expr_stmt|;
name|container
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"ARGH: caught: "
operator|+
name|e
argument_list|)
expr_stmt|;
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
block|}
block|}
argument_list|)
operator|.
name|start
argument_list|()
expr_stmt|;
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"started container"
argument_list|)
expr_stmt|;
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"okay, go play"
argument_list|)
expr_stmt|;
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
name|System
operator|.
name|in
operator|.
name|read
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

