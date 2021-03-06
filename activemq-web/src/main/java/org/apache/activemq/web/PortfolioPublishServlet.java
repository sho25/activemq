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
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|PrintWriter
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Hashtable
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|Destination
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
name|javax
operator|.
name|jms
operator|.
name|Message
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
name|servlet
operator|.
name|ServletException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|servlet
operator|.
name|http
operator|.
name|HttpServletRequest
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|servlet
operator|.
name|http
operator|.
name|HttpServletResponse
import|;
end_import

begin_comment
comment|/**  * A servlet which will publish dummy market data prices  *   *   */
end_comment

begin_class
specifier|public
class|class
name|PortfolioPublishServlet
extends|extends
name|MessageServletSupport
block|{
specifier|private
specifier|static
specifier|final
name|int
name|MAX_DELTA_PERCENT
init|=
literal|1
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|Double
argument_list|>
name|LAST_PRICES
init|=
operator|new
name|Hashtable
argument_list|<
name|String
argument_list|,
name|Double
argument_list|>
argument_list|()
decl_stmt|;
specifier|public
name|void
name|init
parameter_list|()
throws|throws
name|ServletException
block|{
name|super
operator|.
name|init
argument_list|()
expr_stmt|;
block|}
specifier|protected
name|void
name|doGet
parameter_list|(
name|HttpServletRequest
name|request
parameter_list|,
name|HttpServletResponse
name|response
parameter_list|)
throws|throws
name|ServletException
throws|,
name|IOException
block|{
name|PrintWriter
name|out
init|=
name|response
operator|.
name|getWriter
argument_list|()
decl_stmt|;
name|String
index|[]
name|stocks
init|=
name|request
operator|.
name|getParameterValues
argument_list|(
literal|"stocks"
argument_list|)
decl_stmt|;
if|if
condition|(
name|stocks
operator|==
literal|null
operator|||
name|stocks
operator|.
name|length
operator|==
literal|0
condition|)
block|{
name|out
operator|.
name|println
argument_list|(
literal|"<html><body>No<b>stocks</b> query parameter specified. Cannot publish market data</body></html>"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|Integer
name|total
init|=
operator|(
name|Integer
operator|)
name|request
operator|.
name|getSession
argument_list|(
literal|true
argument_list|)
operator|.
name|getAttribute
argument_list|(
literal|"total"
argument_list|)
decl_stmt|;
if|if
condition|(
name|total
operator|==
literal|null
condition|)
block|{
name|total
operator|=
name|Integer
operator|.
name|valueOf
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
name|int
name|count
init|=
name|getNumberOfMessages
argument_list|(
name|request
argument_list|)
decl_stmt|;
name|total
operator|=
name|Integer
operator|.
name|valueOf
argument_list|(
name|total
operator|.
name|intValue
argument_list|()
operator|+
name|count
argument_list|)
expr_stmt|;
name|request
operator|.
name|getSession
argument_list|()
operator|.
name|setAttribute
argument_list|(
literal|"total"
argument_list|,
name|total
argument_list|)
expr_stmt|;
try|try
block|{
name|WebClient
name|client
init|=
name|WebClient
operator|.
name|getWebClient
argument_list|(
name|request
argument_list|)
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
name|count
condition|;
name|i
operator|++
control|)
block|{
name|sendMessage
argument_list|(
name|client
argument_list|,
name|stocks
argument_list|)
expr_stmt|;
block|}
name|out
operator|.
name|print
argument_list|(
literal|"<html><head><meta http-equiv='refresh' content='"
argument_list|)
expr_stmt|;
name|String
name|refreshRate
init|=
name|request
operator|.
name|getParameter
argument_list|(
literal|"refresh"
argument_list|)
decl_stmt|;
if|if
condition|(
name|refreshRate
operator|==
literal|null
operator|||
name|refreshRate
operator|.
name|length
argument_list|()
operator|==
literal|0
condition|)
block|{
name|refreshRate
operator|=
literal|"1"
expr_stmt|;
block|}
name|out
operator|.
name|print
argument_list|(
name|escape
argument_list|(
name|refreshRate
argument_list|)
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"'/></head>"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"<body>Published<b>"
operator|+
name|escape
argument_list|(
name|Integer
operator|.
name|toString
argument_list|(
name|count
argument_list|)
argument_list|)
operator|+
literal|"</b> of "
operator|+
name|escape
argument_list|(
name|Integer
operator|.
name|toString
argument_list|(
name|total
argument_list|)
argument_list|)
operator|+
literal|" price messages.  Refresh = "
operator|+
name|escape
argument_list|(
name|refreshRate
argument_list|)
operator|+
literal|"s"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"</body></html>"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|JMSException
name|e
parameter_list|)
block|{
name|out
operator|.
name|println
argument_list|(
literal|"<html><body>Failed sending price messages due to<b>"
operator|+
name|e
operator|+
literal|"</b></body></html>"
argument_list|)
expr_stmt|;
name|log
argument_list|(
literal|"Failed to send message: "
operator|+
name|e
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
specifier|protected
name|void
name|sendMessage
parameter_list|(
name|WebClient
name|client
parameter_list|,
name|String
index|[]
name|stocks
parameter_list|)
throws|throws
name|JMSException
block|{
name|Session
name|session
init|=
name|client
operator|.
name|getSession
argument_list|()
decl_stmt|;
name|int
name|idx
init|=
literal|0
decl_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
name|idx
operator|=
operator|(
name|int
operator|)
name|Math
operator|.
name|round
argument_list|(
name|stocks
operator|.
name|length
operator|*
name|Math
operator|.
name|random
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|idx
operator|<
name|stocks
operator|.
name|length
condition|)
block|{
break|break;
block|}
block|}
name|String
name|stock
init|=
name|stocks
index|[
name|idx
index|]
decl_stmt|;
name|Destination
name|destination
init|=
name|session
operator|.
name|createTopic
argument_list|(
literal|"STOCKS."
operator|+
name|stock
argument_list|)
decl_stmt|;
name|String
name|stockText
init|=
name|createStockText
argument_list|(
name|stock
argument_list|)
decl_stmt|;
name|log
argument_list|(
literal|"Sending: "
operator|+
name|stockText
operator|+
literal|" on destination: "
operator|+
name|destination
argument_list|)
expr_stmt|;
name|Message
name|message
init|=
name|session
operator|.
name|createTextMessage
argument_list|(
name|stockText
argument_list|)
decl_stmt|;
name|client
operator|.
name|send
argument_list|(
name|destination
argument_list|,
name|message
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|String
name|createStockText
parameter_list|(
name|String
name|stock
parameter_list|)
block|{
name|Double
name|value
init|=
name|LAST_PRICES
operator|.
name|get
argument_list|(
name|stock
argument_list|)
decl_stmt|;
if|if
condition|(
name|value
operator|==
literal|null
condition|)
block|{
name|value
operator|=
operator|new
name|Double
argument_list|(
name|Math
operator|.
name|random
argument_list|()
operator|*
literal|100
argument_list|)
expr_stmt|;
block|}
comment|// lets mutate the value by some percentage
name|double
name|oldPrice
init|=
name|value
operator|.
name|doubleValue
argument_list|()
decl_stmt|;
name|value
operator|=
operator|new
name|Double
argument_list|(
name|mutatePrice
argument_list|(
name|oldPrice
argument_list|)
argument_list|)
expr_stmt|;
name|LAST_PRICES
operator|.
name|put
argument_list|(
name|stock
argument_list|,
name|value
argument_list|)
expr_stmt|;
name|double
name|price
init|=
name|value
operator|.
name|doubleValue
argument_list|()
decl_stmt|;
name|double
name|offer
init|=
name|price
operator|*
literal|1.001
decl_stmt|;
name|String
name|movement
init|=
operator|(
name|price
operator|>
name|oldPrice
operator|)
condition|?
literal|"up"
else|:
literal|"down"
decl_stmt|;
return|return
literal|"<price stock='"
operator|+
name|stock
operator|+
literal|"' bid='"
operator|+
name|price
operator|+
literal|"' offer='"
operator|+
name|offer
operator|+
literal|"' movement='"
operator|+
name|movement
operator|+
literal|"'/>"
return|;
block|}
specifier|protected
name|double
name|mutatePrice
parameter_list|(
name|double
name|price
parameter_list|)
block|{
name|double
name|percentChange
init|=
operator|(
literal|2
operator|*
name|Math
operator|.
name|random
argument_list|()
operator|*
name|MAX_DELTA_PERCENT
operator|)
operator|-
name|MAX_DELTA_PERCENT
decl_stmt|;
return|return
name|price
operator|*
operator|(
literal|100
operator|+
name|percentChange
operator|)
operator|/
literal|100
return|;
block|}
specifier|protected
name|int
name|getNumberOfMessages
parameter_list|(
name|HttpServletRequest
name|request
parameter_list|)
block|{
name|String
name|name
init|=
name|request
operator|.
name|getParameter
argument_list|(
literal|"count"
argument_list|)
decl_stmt|;
if|if
condition|(
name|name
operator|!=
literal|null
condition|)
block|{
return|return
name|Integer
operator|.
name|parseInt
argument_list|(
name|name
argument_list|)
return|;
block|}
return|return
literal|1
return|;
block|}
specifier|protected
name|String
name|escape
parameter_list|(
name|String
name|text
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|java
operator|.
name|net
operator|.
name|URLEncoder
operator|.
name|encode
argument_list|(
name|text
argument_list|,
literal|"UTF-8"
argument_list|)
return|;
block|}
block|}
end_class

end_unit

