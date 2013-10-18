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
name|io
operator|.
name|ByteArrayInputStream
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
name|HttpStatus
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Ignore
import|;
end_import

begin_class
specifier|public
class|class
name|RestPersistentTest
extends|extends
name|JettyTestSupport
block|{
annotation|@
name|Override
specifier|protected
name|boolean
name|isPersistent
parameter_list|()
block|{
comment|// need persistent for post/get
return|return
literal|true
return|;
block|}
specifier|public
name|void
name|testPostAndGetWithQueue
parameter_list|()
throws|throws
name|Exception
block|{
name|postAndGet
argument_list|(
literal|"queue"
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testPostAndGetWithTopic
parameter_list|()
throws|throws
name|Exception
block|{
comment|// TODO: problems with topics
comment|// postAndGet("topic");
block|}
specifier|public
name|void
name|postAndGet
parameter_list|(
name|String
name|destinationType
parameter_list|)
throws|throws
name|Exception
block|{
specifier|final
name|String
name|urlGET
init|=
literal|"http://localhost:8080/message/upcTest?clientId=consumer1&readTimeout=5000&type="
operator|+
name|destinationType
decl_stmt|;
specifier|final
name|String
name|urlPOST
init|=
literal|"http://localhost:8080/message/upcTest?type="
operator|+
name|destinationType
decl_stmt|;
specifier|final
name|String
name|message1
init|=
literal|"<itemPolicy><upc>1001</upc></itemPolicy>"
decl_stmt|;
specifier|final
name|String
name|property1
init|=
literal|"terminalNumber=lane1"
decl_stmt|;
specifier|final
name|String
name|selector1
init|=
literal|"terminalNumber='lane1'"
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
name|httpClient
operator|.
name|setConnectorType
argument_list|(
name|HttpClient
operator|.
name|CONNECTOR_SELECT_CHANNEL
argument_list|)
expr_stmt|;
comment|//post first message
comment|// TODO: a problem with GET before POST
comment|// getMessage(httpClient, urlGET, selector1, null);  //should NOT receive message1
name|postMessage
argument_list|(
name|httpClient
argument_list|,
name|urlPOST
argument_list|,
name|property1
argument_list|,
name|message1
argument_list|)
expr_stmt|;
name|getMessage
argument_list|(
name|httpClient
argument_list|,
name|urlGET
argument_list|,
name|selector1
argument_list|,
name|message1
argument_list|)
expr_stmt|;
comment|//should receive message1
block|}
specifier|private
name|void
name|postMessage
parameter_list|(
name|HttpClient
name|httpClient
parameter_list|,
name|String
name|url
parameter_list|,
name|String
name|properties
parameter_list|,
name|String
name|message
parameter_list|)
throws|throws
name|Exception
block|{
name|ContentExchange
name|contentExchange
init|=
operator|new
name|ContentExchange
argument_list|()
decl_stmt|;
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
name|url
operator|+
literal|"&"
operator|+
name|properties
argument_list|)
expr_stmt|;
comment|//contentExchange.setRequestHeader("accept", "text/xml");
name|contentExchange
operator|.
name|setRequestHeader
argument_list|(
literal|"Content-Type"
argument_list|,
literal|"text/xml"
argument_list|)
expr_stmt|;
name|contentExchange
operator|.
name|setRequestContentSource
argument_list|(
operator|new
name|ByteArrayInputStream
argument_list|(
name|message
operator|.
name|getBytes
argument_list|(
literal|"UTF-8"
argument_list|)
argument_list|)
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
specifier|private
name|void
name|getMessage
parameter_list|(
name|HttpClient
name|httpClient
parameter_list|,
name|String
name|url
parameter_list|,
name|String
name|selector
parameter_list|,
name|String
name|expectedMessage
parameter_list|)
throws|throws
name|Exception
block|{
name|ContentExchange
name|contentExchange
init|=
operator|new
name|ContentExchange
argument_list|(
literal|true
argument_list|)
decl_stmt|;
name|contentExchange
operator|.
name|setURL
argument_list|(
name|url
argument_list|)
expr_stmt|;
name|contentExchange
operator|.
name|setRequestHeader
argument_list|(
literal|"accept"
argument_list|,
literal|"text/xml"
argument_list|)
expr_stmt|;
name|contentExchange
operator|.
name|setRequestHeader
argument_list|(
literal|"Content-Type"
argument_list|,
literal|"text/xml"
argument_list|)
expr_stmt|;
if|if
condition|(
name|selector
operator|!=
literal|null
condition|)
block|{
name|contentExchange
operator|.
name|setRequestHeader
argument_list|(
literal|"selector"
argument_list|,
name|selector
argument_list|)
expr_stmt|;
block|}
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
if|if
condition|(
name|expectedMessage
operator|!=
literal|null
condition|)
block|{
name|assertNotNull
argument_list|(
name|contentExchange
operator|.
name|getResponseContent
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|expectedMessage
argument_list|,
name|contentExchange
operator|.
name|getResponseContent
argument_list|()
operator|.
name|trim
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit
