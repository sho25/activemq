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
name|parsers
operator|.
name|DocumentBuilder
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|parsers
operator|.
name|DocumentBuilderFactory
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
name|apache
operator|.
name|xpath
operator|.
name|CachedXPathAPI
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

begin_import
import|import
name|org
operator|.
name|w3c
operator|.
name|dom
operator|.
name|Document
import|;
end_import

begin_import
import|import
name|org
operator|.
name|w3c
operator|.
name|dom
operator|.
name|traversal
operator|.
name|NodeIterator
import|;
end_import

begin_class
specifier|public
class|class
name|XalanXPathEvaluator
implements|implements
name|XPathExpression
operator|.
name|XPathEvaluator
block|{
specifier|private
specifier|final
name|String
name|xpath
decl_stmt|;
specifier|public
name|XalanXPathEvaluator
parameter_list|(
name|String
name|xpath
parameter_list|)
block|{
name|this
operator|.
name|xpath
operator|=
name|xpath
expr_stmt|;
block|}
specifier|public
name|boolean
name|evaluate
parameter_list|(
name|Message
name|m
parameter_list|)
throws|throws
name|JMSException
block|{
if|if
condition|(
name|m
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
name|m
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
name|m
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
name|m
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
name|DocumentBuilderFactory
name|factory
init|=
name|DocumentBuilderFactory
operator|.
name|newInstance
argument_list|()
decl_stmt|;
name|factory
operator|.
name|setNamespaceAware
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|DocumentBuilder
name|dbuilder
init|=
name|factory
operator|.
name|newDocumentBuilder
argument_list|()
decl_stmt|;
name|Document
name|doc
init|=
name|dbuilder
operator|.
name|parse
argument_list|(
name|inputSource
argument_list|)
decl_stmt|;
name|CachedXPathAPI
name|cachedXPathAPI
init|=
operator|new
name|CachedXPathAPI
argument_list|()
decl_stmt|;
name|NodeIterator
name|iterator
init|=
name|cachedXPathAPI
operator|.
name|selectNodeIterator
argument_list|(
name|doc
argument_list|,
name|xpath
argument_list|)
decl_stmt|;
return|return
name|iterator
operator|.
name|nextNode
argument_list|()
operator|!=
literal|null
return|;
block|}
catch|catch
parameter_list|(
name|Throwable
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
name|DocumentBuilderFactory
name|factory
init|=
name|DocumentBuilderFactory
operator|.
name|newInstance
argument_list|()
decl_stmt|;
name|factory
operator|.
name|setNamespaceAware
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|DocumentBuilder
name|dbuilder
init|=
name|factory
operator|.
name|newDocumentBuilder
argument_list|()
decl_stmt|;
name|Document
name|doc
init|=
name|dbuilder
operator|.
name|parse
argument_list|(
name|inputSource
argument_list|)
decl_stmt|;
comment|// We should associated the cachedXPathAPI object with the message
comment|// being evaluated
comment|// since that should speedup subsequent xpath expressions.
name|CachedXPathAPI
name|cachedXPathAPI
init|=
operator|new
name|CachedXPathAPI
argument_list|()
decl_stmt|;
name|NodeIterator
name|iterator
init|=
name|cachedXPathAPI
operator|.
name|selectNodeIterator
argument_list|(
name|doc
argument_list|,
name|xpath
argument_list|)
decl_stmt|;
return|return
name|iterator
operator|.
name|nextNode
argument_list|()
operator|!=
literal|null
return|;
block|}
catch|catch
parameter_list|(
name|Throwable
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

