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
name|util
operator|.
name|oxm
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
name|java
operator|.
name|io
operator|.
name|StringWriter
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
name|ObjectMessage
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
name|TextMessage
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|transform
operator|.
name|Result
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|transform
operator|.
name|Source
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|transform
operator|.
name|stream
operator|.
name|StreamResult
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|transform
operator|.
name|stream
operator|.
name|StreamSource
import|;
end_import

begin_import
import|import
name|org
operator|.
name|springframework
operator|.
name|oxm
operator|.
name|support
operator|.
name|AbstractMarshaller
import|;
end_import

begin_comment
comment|/**  * Transforms object messages to text messages and vice versa using Spring OXM.  *  */
end_comment

begin_class
specifier|public
class|class
name|OXMMessageTransformer
extends|extends
name|AbstractXMLMessageTransformer
block|{
comment|/** 	 * OXM marshaller used to marshall/unmarshall messages 	 */
specifier|private
name|AbstractMarshaller
name|marshaller
decl_stmt|;
specifier|public
name|AbstractMarshaller
name|getMarshaller
parameter_list|()
block|{
return|return
name|marshaller
return|;
block|}
specifier|public
name|void
name|setMarshaller
parameter_list|(
name|AbstractMarshaller
name|marshaller
parameter_list|)
block|{
name|this
operator|.
name|marshaller
operator|=
name|marshaller
expr_stmt|;
block|}
comment|/**      * Marshalls the Object in the {@link ObjectMessage} to a string using XML      * encoding      */
specifier|protected
name|String
name|marshall
parameter_list|(
name|Session
name|session
parameter_list|,
name|ObjectMessage
name|objectMessage
parameter_list|)
throws|throws
name|JMSException
block|{
try|try
block|{
name|StringWriter
name|writer
init|=
operator|new
name|StringWriter
argument_list|()
decl_stmt|;
name|Result
name|result
init|=
operator|new
name|StreamResult
argument_list|(
name|writer
argument_list|)
decl_stmt|;
name|marshaller
operator|.
name|marshal
argument_list|(
name|objectMessage
operator|.
name|getObject
argument_list|()
argument_list|,
name|result
argument_list|)
expr_stmt|;
name|writer
operator|.
name|flush
argument_list|()
expr_stmt|;
return|return
name|writer
operator|.
name|toString
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|JMSException
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
throw|;
block|}
block|}
comment|/**      * Unmarshalls the XML encoded message in the {@link TextMessage} to an      * Object      */
specifier|protected
name|Object
name|unmarshall
parameter_list|(
name|Session
name|session
parameter_list|,
name|TextMessage
name|textMessage
parameter_list|)
throws|throws
name|JMSException
block|{
try|try
block|{
name|String
name|text
init|=
name|textMessage
operator|.
name|getText
argument_list|()
decl_stmt|;
name|Source
name|source
init|=
operator|new
name|StreamSource
argument_list|(
operator|new
name|StringReader
argument_list|(
name|text
argument_list|)
argument_list|)
decl_stmt|;
return|return
name|marshaller
operator|.
name|unmarshal
argument_list|(
name|source
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|JMSException
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
throw|;
block|}
block|}
block|}
end_class

end_unit

