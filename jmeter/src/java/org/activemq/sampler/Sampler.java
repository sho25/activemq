begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  *  * Copyright 2004 The Apache Software Foundation  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
package|package
name|org
operator|.
name|activemq
operator|.
name|sampler
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|jmeter
operator|.
name|config
operator|.
name|ConfigTestElement
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|jmeter
operator|.
name|samplers
operator|.
name|AbstractSampler
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|jmeter
operator|.
name|samplers
operator|.
name|Entry
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|jmeter
operator|.
name|samplers
operator|.
name|SampleResult
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|jmeter
operator|.
name|util
operator|.
name|JMeterUtils
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|jorphan
operator|.
name|logging
operator|.
name|LoggingManager
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|log
operator|.
name|Logger
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
name|MessageProducer
import|;
end_import

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
name|java
operator|.
name|util
operator|.
name|HashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import

begin_comment
comment|/**  * Should be extended by the Producer and Consumer because this contains  * similar methods and variables that they use.  */
end_comment

begin_class
specifier|public
class|class
name|Sampler
extends|extends
name|AbstractSampler
block|{
specifier|public
specifier|static
specifier|final
name|String
name|FILENAME
init|=
literal|"Sampler.filename"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|CLASSNAME
init|=
literal|"Sampler.classname"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|URL
init|=
literal|"Sampler.url"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|DURATION
init|=
literal|"Sampler.duration"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|RAMP_UP
init|=
literal|"Sampler.ramp_up"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|NOPRODUCER
init|=
literal|"Sampler.noprod"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|NOCONSUMER
init|=
literal|"Sampler.noconsumer"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|NOSUBJECT
init|=
literal|"Sampler.nosubject"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|DURABLE
init|=
literal|"Sampler.durable"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|TOPIC
init|=
literal|"Sampler.topic"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|TOOL_DEFAULT
init|=
literal|"TOOL.DEFAULT"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|MSGSIZE
init|=
literal|"Sampler.msgsize"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|MQSERVER
init|=
literal|"Sampler.mqserver"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|DEFMSGINTERVAL
init|=
literal|"Sampler.defmsginterval"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|MSGINTERVAL
init|=
literal|"Sampler.msginterval"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|TRANSACTED
init|=
literal|"Sampler.transacted"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|BATCHSIZE
init|=
literal|"Sampler.batchsize"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|CONFIG_SUBJECT
init|=
literal|"TOOL.DEFAULT.CONFIG"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|CONFIRM_SUBJECT
init|=
literal|"TOOL.DEFAULT.CONFIRM"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|PUBLISH_MSG
init|=
literal|"true"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|NOMESSAGES
init|=
literal|"Sampler.nomessages"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|ACTIVEMQ_SERVER
init|=
name|JMeterUtils
operator|.
name|getResString
argument_list|(
literal|"activemq_server"
argument_list|)
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|boolean
name|TRANSACTED_FALSE
init|=
literal|false
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|LAST_MESSAGE
init|=
literal|"LAST"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|int
name|TIMEOUT
init|=
literal|1000
decl_stmt|;
specifier|public
specifier|static
name|int
name|duration
decl_stmt|;
specifier|public
specifier|static
name|int
name|ramp_up
decl_stmt|;
specifier|public
specifier|static
name|boolean
name|stopThread
decl_stmt|;
specifier|protected
specifier|static
specifier|final
name|String
name|TCPKEY
init|=
literal|"TCP"
decl_stmt|;
specifier|protected
specifier|static
specifier|final
name|String
name|ERRKEY
init|=
literal|"ERR"
decl_stmt|;
specifier|protected
specifier|transient
name|SamplerClient
name|protocolHandler
decl_stmt|;
specifier|protected
name|String
index|[]
name|subjects
decl_stmt|;
specifier|protected
name|String
index|[]
name|producers
decl_stmt|;
specifier|protected
name|boolean
name|embeddedBroker
init|=
literal|false
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|Logger
name|log
init|=
name|LoggingManager
operator|.
name|getLoggerForClass
argument_list|()
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|protoPrefix
init|=
literal|"org.activemq.sampler."
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|int
name|DEFAULT_DURATION
init|=
literal|5
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|int
name|DEFAULT_RAMP_UP
init|=
literal|1
decl_stmt|;
specifier|private
name|List
name|resources
init|=
operator|new
name|ArrayList
argument_list|()
decl_stmt|;
specifier|private
name|Session
name|session
decl_stmt|;
specifier|private
name|MessageProducer
name|publisher
decl_stmt|;
specifier|private
name|Connection
name|connection
decl_stmt|;
comment|/**      * Used to populate resource.      * @param resource      */
specifier|protected
name|void
name|addResource
parameter_list|(
name|Object
name|resource
parameter_list|)
block|{
name|resources
operator|.
name|add
argument_list|(
name|resource
argument_list|)
expr_stmt|;
block|}
comment|/**      * Return the object producer class name.      *      * @return Returns the classname of the producer.      */
specifier|protected
specifier|static
name|String
name|getClassname
parameter_list|()
block|{
name|String
name|className
init|=
name|JMeterUtils
operator|.
name|getPropDefault
argument_list|(
literal|"tcp.prod.handler"
argument_list|,
literal|"SamplerClientImpl"
argument_list|)
decl_stmt|;
return|return
name|className
return|;
block|}
comment|/**      * Returns a formatted string label describing this sampler      * Example output:      * Tcp://Tcp.nowhere.com/pub/README.txt      *      * @return a formatted string label describing this sampler      */
specifier|protected
name|String
name|getLabel
parameter_list|()
block|{
return|return
operator|(
name|this
operator|.
name|getURL
argument_list|()
operator|)
return|;
block|}
comment|/**      * @return Returns the password.      **/
comment|/*protected String getPassword() {         return getPropertyAsString(ConfigTestElement.PASSWORD);     } */
comment|/**      * Retrieves the protocol.      *      * @return Returns the protocol.      */
specifier|protected
name|SamplerClient
name|getProtocol
parameter_list|()
block|{
name|SamplerClient
name|samplerClient
init|=
literal|null
decl_stmt|;
name|Class
name|javaClass
init|=
name|getClass
argument_list|(
name|getClassname
argument_list|()
argument_list|)
decl_stmt|;
try|try
block|{
name|samplerClient
operator|=
operator|(
name|SamplerClient
operator|)
name|javaClass
operator|.
name|newInstance
argument_list|()
expr_stmt|;
if|if
condition|(
name|log
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|log
operator|.
name|debug
argument_list|(
operator|new
name|StringBuffer
argument_list|()
operator|.
name|append
argument_list|(
name|this
operator|+
literal|"Created: "
argument_list|)
operator|.
name|append
argument_list|(
name|getClassname
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|"@"
argument_list|)
operator|.
name|append
argument_list|(
name|Integer
operator|.
name|toHexString
argument_list|(
name|samplerClient
operator|.
name|hashCode
argument_list|()
argument_list|)
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|log
operator|.
name|error
argument_list|(
name|this
operator|+
literal|" Exception creating: "
operator|+
name|getClassname
argument_list|()
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
return|return
name|samplerClient
return|;
block|}
comment|/**      * @return Returns the username.      */
specifier|protected
name|String
name|getUsername
parameter_list|()
block|{
return|return
name|getPropertyAsString
argument_list|(
name|ConfigTestElement
operator|.
name|USERNAME
argument_list|)
return|;
block|}
comment|/**      * @return Returns the timeout int object.      */
specifier|protected
name|int
name|getTimeout
parameter_list|()
block|{
return|return
name|TIMEOUT
return|;
block|}
comment|/**      * Returns the Class object of the running producer.      *      * @param className      * @return      */
specifier|protected
name|Class
name|getClass
parameter_list|(
name|String
name|className
parameter_list|)
block|{
name|Class
name|c
init|=
literal|null
decl_stmt|;
try|try
block|{
name|c
operator|=
name|Class
operator|.
name|forName
argument_list|(
name|className
argument_list|,
literal|false
argument_list|,
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|getContextClassLoader
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ClassNotFoundException
name|e
parameter_list|)
block|{
try|try
block|{
name|c
operator|=
name|Class
operator|.
name|forName
argument_list|(
name|protoPrefix
operator|+
name|className
argument_list|,
literal|false
argument_list|,
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|getContextClassLoader
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ClassNotFoundException
name|e1
parameter_list|)
block|{
name|log
operator|.
name|error
argument_list|(
literal|"Could not find protocol class "
operator|+
name|className
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|c
return|;
block|}
comment|/**      * @param newDurable - the new durable to set.      */
specifier|protected
name|void
name|setDurable
parameter_list|(
name|String
name|newDurable
parameter_list|)
block|{
name|this
operator|.
name|setProperty
argument_list|(
name|DURABLE
argument_list|,
name|newDurable
argument_list|)
expr_stmt|;
block|}
comment|/**      * @return Returns whether message is durable.      */
specifier|protected
name|boolean
name|getDurable
parameter_list|()
block|{
return|return
name|getPropertyAsBoolean
argument_list|(
name|DURABLE
argument_list|)
return|;
block|}
comment|/**      * @param newDuration - the new duration to set.      */
specifier|protected
name|void
name|setDuration
parameter_list|(
name|String
name|newDuration
parameter_list|)
block|{
name|this
operator|.
name|setProperty
argument_list|(
name|DURATION
argument_list|,
name|newDuration
argument_list|)
expr_stmt|;
block|}
comment|/**      * @return Returns the duration.      */
specifier|protected
name|int
name|getDuration
parameter_list|()
block|{
return|return
name|getPropertyAsInt
argument_list|(
name|DURATION
argument_list|)
return|;
block|}
comment|/**      * @param embeddedBroker - new value.      */
specifier|protected
name|void
name|setEmbeddedBroker
parameter_list|(
name|boolean
name|embeddedBroker
parameter_list|)
block|{
name|this
operator|.
name|embeddedBroker
operator|=
name|embeddedBroker
expr_stmt|;
block|}
comment|/**      * @return Returns embeddedBroker.      */
specifier|protected
name|boolean
name|getEmbeddedBroker
parameter_list|()
block|{
return|return
name|embeddedBroker
return|;
block|}
comment|/**      * @param newFilename - the new filename to set.      */
specifier|protected
name|void
name|setFilename
parameter_list|(
name|String
name|newFilename
parameter_list|)
block|{
name|this
operator|.
name|setProperty
argument_list|(
name|FILENAME
argument_list|,
name|newFilename
argument_list|)
expr_stmt|;
block|}
comment|/**      * @return Returns the filename.      */
specifier|protected
name|String
name|getFilename
parameter_list|()
block|{
return|return
name|getPropertyAsString
argument_list|(
name|FILENAME
argument_list|)
return|;
block|}
comment|/**      * @param newMsgSize - the new message size to set.      */
specifier|protected
name|void
name|setMsgSize
parameter_list|(
name|String
name|newMsgSize
parameter_list|)
block|{
name|this
operator|.
name|setProperty
argument_list|(
name|MSGSIZE
argument_list|,
name|newMsgSize
argument_list|)
expr_stmt|;
block|}
comment|/**      * @return Returns the message size.      */
specifier|protected
name|int
name|getMsgSize
parameter_list|()
block|{
return|return
name|getPropertyAsInt
argument_list|(
name|MSGSIZE
argument_list|)
return|;
block|}
comment|/**      * @param newNoCons - the number of consumer to set.      */
specifier|protected
name|void
name|setNoConsumer
parameter_list|(
name|String
name|newNoCons
parameter_list|)
block|{
name|this
operator|.
name|setProperty
argument_list|(
name|NOCONSUMER
argument_list|,
name|newNoCons
argument_list|)
expr_stmt|;
block|}
comment|/**      * @return Returns the number of producers.      */
specifier|protected
name|int
name|getNoConsumer
parameter_list|()
block|{
return|return
name|getPropertyAsInt
argument_list|(
name|NOCONSUMER
argument_list|)
return|;
block|}
comment|/**      * @param newNoProd - the number of producer to set.      */
specifier|protected
name|void
name|setNoProducer
parameter_list|(
name|String
name|newNoProd
parameter_list|)
block|{
name|this
operator|.
name|setProperty
argument_list|(
name|NOPRODUCER
argument_list|,
name|newNoProd
argument_list|)
expr_stmt|;
block|}
comment|/**      * @return Returns the number of producers.      */
specifier|protected
name|int
name|getNoProducer
parameter_list|()
block|{
return|return
name|getPropertyAsInt
argument_list|(
name|NOPRODUCER
argument_list|)
return|;
block|}
comment|/**      * @param newNoSubject - the new number of subject to set.      */
specifier|protected
name|void
name|setNoSubject
parameter_list|(
name|String
name|newNoSubject
parameter_list|)
block|{
name|this
operator|.
name|setProperty
argument_list|(
name|NOSUBJECT
argument_list|,
name|newNoSubject
argument_list|)
expr_stmt|;
block|}
comment|/**      * @return Return the number of subject.      */
specifier|protected
name|int
name|getNoSubject
parameter_list|()
block|{
return|return
name|getPropertyAsInt
argument_list|(
name|NOSUBJECT
argument_list|)
return|;
block|}
comment|/**      * @param newRampUp - the new ramp up to set.      */
specifier|protected
name|void
name|setRampUp
parameter_list|(
name|String
name|newRampUp
parameter_list|)
block|{
name|this
operator|.
name|setProperty
argument_list|(
name|RAMP_UP
argument_list|,
name|newRampUp
argument_list|)
expr_stmt|;
block|}
comment|/**      * @return Returns the ramp up.      */
specifier|protected
name|int
name|getRampUp
parameter_list|()
block|{
return|return
name|getPropertyAsInt
argument_list|(
name|RAMP_UP
argument_list|)
return|;
block|}
comment|/**      * @param newTopic - the new topic to set.      */
specifier|protected
name|void
name|setTopic
parameter_list|(
name|String
name|newTopic
parameter_list|)
block|{
name|this
operator|.
name|setProperty
argument_list|(
name|TOPIC
argument_list|,
name|newTopic
argument_list|)
expr_stmt|;
block|}
comment|/**      * @return Return whether the message is topic.      */
specifier|protected
name|boolean
name|getTopic
parameter_list|()
block|{
return|return
name|getPropertyAsBoolean
argument_list|(
name|TOPIC
argument_list|)
return|;
block|}
comment|/**      * @param newURL - the new url to set.      */
specifier|protected
name|void
name|setURL
parameter_list|(
name|String
name|newURL
parameter_list|)
block|{
name|this
operator|.
name|setProperty
argument_list|(
name|URL
argument_list|,
name|newURL
argument_list|)
expr_stmt|;
block|}
comment|/**      * @return Returns the url.      */
specifier|protected
name|String
name|getURL
parameter_list|()
block|{
return|return
name|getPropertyAsString
argument_list|(
name|URL
argument_list|)
return|;
block|}
comment|/**      * @param newMQServer - the new message size to set.      */
specifier|protected
name|void
name|setMQServer
parameter_list|(
name|String
name|newMQServer
parameter_list|)
block|{
name|this
operator|.
name|setProperty
argument_list|(
name|MQSERVER
argument_list|,
name|newMQServer
argument_list|)
expr_stmt|;
block|}
comment|/**      * @return Returns the message queue server name.      */
specifier|public
name|String
name|getMQServer
parameter_list|()
block|{
return|return
name|getPropertyAsString
argument_list|(
name|MQSERVER
argument_list|)
return|;
block|}
comment|/**      * @param newDefMsgInterval - set to use or not the default message interval.      */
specifier|protected
name|void
name|setDefMsgInterval
parameter_list|(
name|String
name|newDefMsgInterval
parameter_list|)
block|{
name|this
operator|.
name|setProperty
argument_list|(
name|DEFMSGINTERVAL
argument_list|,
name|newDefMsgInterval
argument_list|)
expr_stmt|;
block|}
comment|/**      * @return Returns whether to use Default Message Interval.      */
specifier|protected
name|boolean
name|getDefMsgInterval
parameter_list|()
block|{
return|return
name|getPropertyAsBoolean
argument_list|(
name|DEFMSGINTERVAL
argument_list|)
return|;
block|}
comment|/**      * @param newMsgInterval - the new Message Interval to set.      */
specifier|protected
name|void
name|setMsgInterval
parameter_list|(
name|String
name|newMsgInterval
parameter_list|)
block|{
name|this
operator|.
name|setProperty
argument_list|(
name|MSGINTERVAL
argument_list|,
name|newMsgInterval
argument_list|)
expr_stmt|;
block|}
comment|/**      * @return Returns the message interval.      */
specifier|protected
name|int
name|getMsgInterval
parameter_list|()
block|{
return|return
name|getPropertyAsInt
argument_list|(
name|MSGINTERVAL
argument_list|)
return|;
block|}
comment|/**      *      * @param insession - the new Session to set.      */
specifier|protected
name|void
name|setSession
parameter_list|(
name|Session
name|insession
parameter_list|)
block|{
name|session
operator|=
name|insession
expr_stmt|;
block|}
comment|/**      *      * @return Returns the session.      */
specifier|protected
name|Session
name|getSession
parameter_list|()
block|{
return|return
name|session
return|;
block|}
comment|/**      * @return Returns whether to use Transacted type.      */
specifier|protected
name|boolean
name|getTransacted
parameter_list|()
block|{
return|return
name|getPropertyAsBoolean
argument_list|(
name|TRANSACTED
argument_list|)
return|;
block|}
comment|/**      * @param newTransacted - when to use Transacted type.      */
specifier|protected
name|void
name|setTransacted
parameter_list|(
name|String
name|newTransacted
parameter_list|)
block|{
name|this
operator|.
name|setProperty
argument_list|(
name|TRANSACTED
argument_list|,
name|newTransacted
argument_list|)
expr_stmt|;
block|}
comment|/**      * @param newBatchSize - the new Batch size to set.      */
specifier|protected
name|void
name|setBatchSize
parameter_list|(
name|String
name|newBatchSize
parameter_list|)
block|{
name|this
operator|.
name|setProperty
argument_list|(
name|BATCHSIZE
argument_list|,
name|newBatchSize
argument_list|)
expr_stmt|;
block|}
comment|/**      * @return Returns the Batch Size.      */
specifier|protected
name|int
name|getBatchSize
parameter_list|()
block|{
return|return
name|getPropertyAsInt
argument_list|(
name|BATCHSIZE
argument_list|)
return|;
block|}
comment|/**      *      * @param inpublisher - MessageProducer object      */
specifier|protected
name|void
name|setPublisher
parameter_list|(
name|MessageProducer
name|inpublisher
parameter_list|)
block|{
name|publisher
operator|=
name|inpublisher
expr_stmt|;
block|}
comment|/**      *      * @return publisher - MessageProducer object.      */
specifier|protected
name|MessageProducer
name|getPublisher
parameter_list|()
block|{
return|return
name|publisher
return|;
block|}
comment|/**      * @param inconnection - connection.      */
specifier|protected
name|void
name|setConnection
parameter_list|(
name|Connection
name|inconnection
parameter_list|)
block|{
name|connection
operator|=
name|inconnection
expr_stmt|;
block|}
comment|/**      * @return Returns the connection.      */
specifier|protected
name|Connection
name|getConnection
parameter_list|()
block|{
return|return
name|connection
return|;
block|}
comment|/**      * @param msgcount - the number of messges to send.      */
specifier|protected
name|void
name|setNoMessages
parameter_list|(
name|String
name|msgcount
parameter_list|)
block|{
name|this
operator|.
name|setProperty
argument_list|(
name|NOMESSAGES
argument_list|,
name|msgcount
argument_list|)
expr_stmt|;
block|}
comment|/**      * @return Returns the number of messages to send.      */
specifier|protected
name|int
name|getNoMessages
parameter_list|()
block|{
return|return
name|getPropertyAsInt
argument_list|(
name|NOMESSAGES
argument_list|)
return|;
block|}
comment|/**      *      * @return Return the Config Message.      */
specifier|protected
name|String
name|getConfigMessage
parameter_list|()
block|{
name|StringBuffer
name|sb
init|=
operator|new
name|StringBuffer
argument_list|()
decl_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"#"
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|this
operator|.
name|getNoMessages
argument_list|()
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"#"
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|this
operator|.
name|getNoProducer
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
comment|// provides an empty implementation, this will be properly implemented
comment|// by its subclass.
specifier|public
name|SampleResult
name|sample
parameter_list|(
name|Entry
name|entry
parameter_list|)
block|{
return|return
literal|null
return|;
comment|//To change body of implemented methods use File | Settings | File Templates.
block|}
comment|/**      * Creates the subject that will be published.      */
specifier|public
name|void
name|start
parameter_list|()
block|{
comment|// create the subjects.
name|subjects
operator|=
operator|new
name|String
index|[
name|getNoSubject
argument_list|()
index|]
expr_stmt|;
comment|// appended to the subject to determine if its a queue or topic.
name|String
name|prefix
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|this
operator|.
name|getTopic
argument_list|()
condition|)
block|{
name|prefix
operator|=
literal|".TOPIC"
expr_stmt|;
block|}
else|else
block|{
name|prefix
operator|=
literal|".QUEUE"
expr_stmt|;
block|}
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|subjects
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|subjects
index|[
name|i
index|]
operator|=
name|TOOL_DEFAULT
operator|+
name|prefix
operator|+
name|i
expr_stmt|;
block|}
comment|// set the duration.
if|if
condition|(
name|getDuration
argument_list|()
operator|==
literal|0
condition|)
block|{
name|duration
operator|=
name|DEFAULT_DURATION
expr_stmt|;
block|}
else|else
block|{
name|duration
operator|=
name|getDuration
argument_list|()
expr_stmt|;
block|}
comment|// set the ramp_up.
if|if
condition|(
name|getRampUp
argument_list|()
operator|==
literal|0
condition|)
block|{
name|ramp_up
operator|=
name|DEFAULT_RAMP_UP
expr_stmt|;
block|}
else|else
block|{
name|ramp_up
operator|=
name|getRampUp
argument_list|()
expr_stmt|;
block|}
comment|// set the thread to start.
name|stopThread
operator|=
literal|false
expr_stmt|;
block|}
specifier|protected
name|String
index|[]
name|getSubjects
parameter_list|()
block|{
comment|// create the subjects.
name|String
index|[]
name|subjects
init|=
operator|new
name|String
index|[
name|getNoSubject
argument_list|()
index|]
decl_stmt|;
comment|// appended to the subject to determine if its a queue or topic.
name|String
name|prefix
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|this
operator|.
name|getTopic
argument_list|()
condition|)
block|{
name|prefix
operator|=
literal|".TOPIC"
expr_stmt|;
block|}
else|else
block|{
name|prefix
operator|=
literal|".QUEUE"
expr_stmt|;
block|}
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|subjects
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|subjects
index|[
name|i
index|]
operator|=
name|TOOL_DEFAULT
operator|+
name|prefix
operator|+
name|i
expr_stmt|;
block|}
return|return
name|subjects
return|;
block|}
comment|/**      * the cache of TCP Connections      */
specifier|protected
specifier|static
name|ThreadLocal
name|tp
init|=
operator|new
name|ThreadLocal
argument_list|()
block|{
specifier|protected
name|Object
name|initialValue
parameter_list|()
block|{
return|return
operator|new
name|HashMap
argument_list|()
return|;
block|}
block|}
decl_stmt|;
block|}
end_class

end_unit

