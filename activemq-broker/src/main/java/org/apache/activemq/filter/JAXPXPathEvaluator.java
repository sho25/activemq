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
name|filter
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|StringReader
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
name|JMSException
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
name|xml
operator|.
name|xpath
operator|.
name|XPath
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|xpath
operator|.
name|XPathConstants
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|xpath
operator|.
name|XPathExpressionException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|xpath
operator|.
name|XPathFactory
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
name|Message
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
name|ByteArrayInputStream
import|;
end_import

begin_import
import|import
name|org
operator|.
name|xml
operator|.
name|sax
operator|.
name|InputSource
import|;
end_import

begin_class
specifier|public
class|class
name|JAXPXPathEvaluator
implements|implements
name|XPathExpression
operator|.
name|XPathEvaluator
block|{
specifier|private
specifier|static
specifier|final
name|XPathFactory
name|FACTORY
init|=
name|XPathFactory
operator|.
name|newInstance
argument_list|()
decl_stmt|;
specifier|private
name|javax
operator|.
name|xml
operator|.
name|xpath
operator|.
name|XPathExpression
name|expression
decl_stmt|;
specifier|public
name|JAXPXPathEvaluator
parameter_list|(
name|String
name|xpathExpression
parameter_list|)
block|{
try|try
block|{
name|XPath
name|xpath
init|=
name|FACTORY
operator|.
name|newXPath
argument_list|()
decl_stmt|;
name|expression
operator|=
name|xpath
operator|.
name|compile
argument_list|(
name|xpathExpression
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|XPathExpressionException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Invalid XPath expression: "
operator|+
name|xpathExpression
argument_list|)
throw|;
block|}
block|}
specifier|public
name|boolean
name|evaluate
parameter_list|(
name|Message
name|message
parameter_list|)
throws|throws
name|JMSException
block|{
if|if
condition|(
name|message
operator|instanceof
name|TextMessage
condition|)
block|{
name|String
name|text
init|=
operator|(
operator|(
name|TextMessage
operator|)
name|message
operator|)
operator|.
name|getText
argument_list|()
decl_stmt|;
return|return
name|evaluate
argument_list|(
name|text
argument_list|)
return|;
block|}
elseif|else
if|if
condition|(
name|message
operator|instanceof
name|BytesMessage
condition|)
block|{
name|BytesMessage
name|bm
init|=
operator|(
name|BytesMessage
operator|)
name|message
decl_stmt|;
name|byte
name|data
index|[]
init|=
operator|new
name|byte
index|[
operator|(
name|int
operator|)
name|bm
operator|.
name|getBodyLength
argument_list|()
index|]
decl_stmt|;
name|bm
operator|.
name|readBytes
argument_list|(
name|data
argument_list|)
expr_stmt|;
return|return
name|evaluate
argument_list|(
name|data
argument_list|)
return|;
block|}
return|return
literal|false
return|;
block|}
specifier|private
name|boolean
name|evaluate
parameter_list|(
name|byte
index|[]
name|data
parameter_list|)
block|{
try|try
block|{
name|InputSource
name|inputSource
init|=
operator|new
name|InputSource
argument_list|(
operator|new
name|ByteArrayInputStream
argument_list|(
name|data
argument_list|)
argument_list|)
decl_stmt|;
return|return
operator|(
operator|(
name|Boolean
operator|)
name|expression
operator|.
name|evaluate
argument_list|(
name|inputSource
argument_list|,
name|XPathConstants
operator|.
name|BOOLEAN
argument_list|)
operator|)
operator|.
name|booleanValue
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|XPathExpressionException
name|e
parameter_list|)
block|{
return|return
literal|false
return|;
block|}
block|}
specifier|private
name|boolean
name|evaluate
parameter_list|(
name|String
name|text
parameter_list|)
block|{
try|try
block|{
name|InputSource
name|inputSource
init|=
operator|new
name|InputSource
argument_list|(
operator|new
name|StringReader
argument_list|(
name|text
argument_list|)
argument_list|)
decl_stmt|;
return|return
operator|(
operator|(
name|Boolean
operator|)
name|expression
operator|.
name|evaluate
argument_list|(
name|inputSource
argument_list|,
name|XPathConstants
operator|.
name|BOOLEAN
argument_list|)
operator|)
operator|.
name|booleanValue
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|XPathExpressionException
name|e
parameter_list|)
block|{
return|return
literal|false
return|;
block|}
block|}
block|}
end_class

end_unit
