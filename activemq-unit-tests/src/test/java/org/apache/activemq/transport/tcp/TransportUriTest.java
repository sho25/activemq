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
name|tcp
package|;
end_package

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|Connection
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

begin_import
import|import
name|junit
operator|.
name|framework
operator|.
name|Test
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
name|EmbeddedBrokerTestSupport
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

begin_comment
comment|/**  *  */
end_comment

begin_class
specifier|public
class|class
name|TransportUriTest
extends|extends
name|EmbeddedBrokerTestSupport
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
name|TransportUriTest
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|DIFF_SERV
init|=
literal|"&diffServ="
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|TOS
init|=
literal|"&typeOfService="
decl_stmt|;
specifier|protected
name|Connection
name|connection
decl_stmt|;
specifier|public
name|String
name|prefix
decl_stmt|;
specifier|public
name|String
name|postfix
decl_stmt|;
comment|//    public void initCombosForTestUriOptionsWork() {
comment|//        initSharedCombos();
comment|//    }
comment|//
comment|//    public void testUriOptionsWork() throws Exception {
comment|//        String uri = prefix + bindAddress + postfix;
comment|//        LOG.info("Connecting via: " + uri);
comment|//
comment|//        connection = new ActiveMQConnectionFactory(uri).createConnection();
comment|//        connection.start();
comment|//    }
comment|//
comment|//    public void initCombosForTestValidDiffServOptionsWork() {
comment|//        initSharedCombos();
comment|//    }
comment|//
comment|//    public void testValidDiffServOptionsWork() throws Exception {
comment|//        String[] validIntegerOptions = {"0", "1", "32", "62", "63"};
comment|//        for (String opt : validIntegerOptions) {
comment|//            testValidOptionsWork(DIFF_SERV + opt, "");
comment|//        }
comment|//        String[] validNameOptions = { "CS0", "CS1", "CS2", "CS3", "CS4", "CS5", "CS6",
comment|//                "CS7", "EF", "AF11", "AF12","AF13", "AF21", "AF22", "AF23", "AF31",
comment|//                "AF32", "AF33", "AF41", "AF42", "AF43" };
comment|//        for (String opt : validNameOptions) {
comment|//            testValidOptionsWork(DIFF_SERV + opt, "");
comment|//        }
comment|//    }
comment|//
comment|//    public void initCombosForTestInvalidDiffServOptionDoesNotWork() {
comment|//        initSharedCombos();
comment|//    }
comment|//
comment|//    public void testInvalidDiffServOptionsDoesNotWork() throws Exception {
comment|//        String[] invalidIntegerOptions = {"-2", "-1", "64", "65", "100", "255"};
comment|//        for (String opt : invalidIntegerOptions) {
comment|//            testInvalidOptionsDoNotWork(DIFF_SERV + opt, "");
comment|//        }
comment|//        String[] invalidNameOptions = {"hi", "", "A", "AF", "-AF21"};
comment|//        for (String opt : invalidNameOptions) {
comment|//            testInvalidOptionsDoNotWork(DIFF_SERV + opt, "");
comment|//        }
comment|//    }
comment|//
comment|//    public void initCombosForTestValidTypeOfServiceOptionsWork() {
comment|//        initSharedCombos();
comment|//    }
comment|//
comment|//    public void testValidTypeOfServiceOptionsWork() throws Exception {
comment|//        int[] validOptions = {0, 1, 32, 100, 254, 255};
comment|//        for (int opt : validOptions) {
comment|//            testValidOptionsWork(TOS + opt, "");
comment|//        }
comment|//    }
comment|//
comment|//    public void initCombosForTestInvalidTypeOfServiceOptionDoesNotWork() {
comment|//        initSharedCombos();
comment|//    }
comment|//
comment|//    public void testInvalidTypeOfServiceOptionDoesNotWork() throws Exception {
comment|//        int[] invalidOptions = {-2, -1, 256, 257};
comment|//        for (int opt : invalidOptions) {
comment|//            testInvalidOptionsDoNotWork(TOS + opt, "");
comment|//        }
comment|//    }
comment|//
comment|//    public void initCombosForTestDiffServAndTypeOfServiceMutuallyExclusive() {
comment|//        initSharedCombos();
comment|//    }
comment|//
comment|//    public void testDiffServAndTypeServiceMutuallyExclusive() {
comment|//        String msg = "It should not be possible to set both Differentiated "
comment|//            + "Services and Type of Service options on the same connection "
comment|//            + "URI.";
comment|//        testInvalidOptionsDoNotWork(TOS + 32 + DIFF_SERV, msg);
comment|//        testInvalidOptionsDoNotWork(DIFF_SERV + 32 + TOS + 32, msg);
comment|//    }
comment|//
specifier|public
name|void
name|initCombosForTestBadVersionNumberDoesNotWork
parameter_list|()
block|{
name|initSharedCombos
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|testBadVersionNumberDoesNotWork
parameter_list|()
throws|throws
name|Exception
block|{
name|testInvalidOptionsDoNotWork
argument_list|(
literal|"&minmumWireFormatVersion=65535"
argument_list|,
literal|""
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|initCombosForTestBadPropertyNameFails
parameter_list|()
block|{
name|initSharedCombos
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|testBadPropertyNameFails
parameter_list|()
throws|throws
name|Exception
block|{
name|testInvalidOptionsDoNotWork
argument_list|(
literal|"&cheese=abc"
argument_list|,
literal|""
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|initSharedCombos
parameter_list|()
block|{
name|addCombinationValues
argument_list|(
literal|"prefix"
argument_list|,
operator|new
name|Object
index|[]
block|{
literal|""
block|}
argument_list|)
expr_stmt|;
comment|// TODO: Add more combinations.
name|addCombinationValues
argument_list|(
literal|"postfix"
argument_list|,
operator|new
name|Object
index|[]
block|{
literal|"?tcpNoDelay=true&keepAlive=true&soLinger=0"
block|}
argument_list|)
expr_stmt|;
name|addCombinationValues
argument_list|(
literal|"postfix"
argument_list|,
operator|new
name|Object
index|[]
block|{
literal|"?tcpNoDelay=true&keepAlive=true&soLinger=-1"
block|}
argument_list|)
expr_stmt|;
block|}
comment|//    private void testValidOptionsWork(String options, String msg) {
comment|//        String uri = prefix + bindAddress + postfix + options;
comment|//        LOG.info("Connecting via: " + uri);
comment|//
comment|//        try {
comment|//            connection = new ActiveMQConnectionFactory(uri).createConnection();
comment|//            connection.start();
comment|//        } catch (Exception unexpected) {
comment|//            fail("Valid options '" + options + "' on URI '" + uri + "' should "
comment|//                 + "not have caused an exception to be thrown. " + msg
comment|//                 + " Exception: " + unexpected);
comment|//        }
comment|//    }
specifier|private
name|void
name|testInvalidOptionsDoNotWork
parameter_list|(
name|String
name|options
parameter_list|,
name|String
name|msg
parameter_list|)
block|{
name|String
name|uri
init|=
name|prefix
operator|+
name|bindAddress
operator|+
name|postfix
operator|+
name|options
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Connecting via: "
operator|+
name|uri
argument_list|)
expr_stmt|;
try|try
block|{
name|connection
operator|=
operator|new
name|ActiveMQConnectionFactory
argument_list|(
name|uri
argument_list|)
operator|.
name|createConnection
argument_list|()
expr_stmt|;
name|connection
operator|.
name|start
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"Invalid options '"
operator|+
name|options
operator|+
literal|"' on URI '"
operator|+
name|uri
operator|+
literal|"' should"
operator|+
literal|" have caused an exception to be thrown. "
operator|+
name|msg
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|expected
parameter_list|)
block|{         }
block|}
annotation|@
name|Override
specifier|protected
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|bindAddress
operator|=
literal|"tcp://localhost:61616"
expr_stmt|;
name|super
operator|.
name|setUp
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
if|if
condition|(
name|connection
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|connection
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|JMSException
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
block|}
name|super
operator|.
name|tearDown
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|BrokerService
name|createBroker
parameter_list|()
throws|throws
name|Exception
block|{
name|BrokerService
name|answer
init|=
operator|new
name|BrokerService
argument_list|()
decl_stmt|;
name|answer
operator|.
name|setUseJmx
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|answer
operator|.
name|setPersistent
argument_list|(
name|isPersistent
argument_list|()
argument_list|)
expr_stmt|;
name|answer
operator|.
name|addConnector
argument_list|(
name|bindAddress
argument_list|)
expr_stmt|;
return|return
name|answer
return|;
block|}
specifier|public
specifier|static
name|Test
name|suite
parameter_list|()
block|{
return|return
name|suite
argument_list|(
name|TransportUriTest
operator|.
name|class
argument_list|)
return|;
block|}
block|}
end_class

end_unit

