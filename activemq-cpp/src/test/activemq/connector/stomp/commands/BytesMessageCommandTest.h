/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

#ifndef _ACTIVEMQ_CONNECTOR_STOMP_COMMANDS_BYTESMESSAGECOMMANDTEST_H_
#define _ACTIVEMQ_CONNECTOR_STOMP_COMMANDS_BYTESMESSAGECOMMANDTEST_H_

#include <cppunit/TestFixture.h>
#include <cppunit/extensions/HelperMacros.h>
#include <activemq/connector/stomp/StompTopic.h>
#include <cms/Message.h>

#include <activemq/connector/stomp/commands/BytesMessageCommand.h>

namespace activemq{
namespace connector{
namespace stomp{
namespace commands{

    class BytesMessageCommandTest : public CppUnit::TestFixture
    {
        CPPUNIT_TEST_SUITE( BytesMessageCommandTest );
        CPPUNIT_TEST( test );
        CPPUNIT_TEST_SUITE_END();

    protected:

        class TestAckHandler : public core::ActiveMQAckHandler
        {
        public:
        
            TestAckHandler(void) { wasAcked = false; }
            virtual ~TestAckHandler(void) {}
            
            virtual void acknowledgeMessage( const core::ActiveMQMessage* message)
                throw ( cms::CMSException ) 
            {
                wasAcked = true;
            }
            
        public:
        
            bool wasAcked;

        };

    public:

    	BytesMessageCommandTest() {}
    	virtual ~BytesMessageCommandTest() {}

        void test(void)
        {
            TestAckHandler ackHandler;
            BytesMessageCommand cmd;

            CPPUNIT_ASSERT( cmd.getStompCommandId() == 
                            CommandConstants::SEND );
            
            CPPUNIT_ASSERT( cmd.isResponseRequired() == false );
            cmd.setResponseRequired( true );
            cmd.setCommandId( 123 );
            CPPUNIT_ASSERT( cmd.isResponseRequired() == true );
            CPPUNIT_ASSERT( cmd.getCommandId() == 123 );
            cmd.setCorrelationId( 99 );
            CPPUNIT_ASSERT( cmd.getCorrelationId() == 99 );
            CPPUNIT_ASSERT( cmd.getTransactionId() == NULL );
            cmd.setTransactionId( "ID:123456" );
            CPPUNIT_ASSERT( std::string( cmd.getTransactionId() ) == 
                            "ID:123456" );
            StompTopic topic("testTopic");
            cmd.setCMSDestination( &topic );
            CPPUNIT_ASSERT( cmd.getCMSDestination()->toString() == 
                            "testTopic" );
            
            StompFrame* frame = cmd.marshal().clone();
            
            CPPUNIT_ASSERT( frame != NULL );
            
            BytesMessageCommand cmd1( frame );
            
            CPPUNIT_ASSERT( cmd.getCommandId() == cmd1.getCommandId() );
            CPPUNIT_ASSERT( cmd.getStompCommandId() == cmd1.getStompCommandId() );
            CPPUNIT_ASSERT( cmd.isResponseRequired() == cmd1.isResponseRequired() );
            CPPUNIT_ASSERT( cmd.getCorrelationId() == cmd1.getCorrelationId() );
            CPPUNIT_ASSERT( std::string(cmd.getTransactionId()) == cmd1.getTransactionId() );
            
            cmd.setAckHandler( &ackHandler );
            cmd.acknowledge();
            CPPUNIT_ASSERT( ackHandler.wasAcked == true );
            
            CPPUNIT_ASSERT( 
                cmd.getProperties().hasProperty( "test" ) == false );
            cmd.getProperties().setProperty( "test", "value" );
            CPPUNIT_ASSERT( 
                cmd.getProperties().hasProperty( "test" ) == true );
            CPPUNIT_ASSERT( 
                std::string( cmd.getProperties().getProperty( "test" ) ) == "value" );
                
            CPPUNIT_ASSERT( cmd.getCMSCorrelationId() == NULL );
            cmd.setCMSCorrelationId( "ID:1234567" );
            CPPUNIT_ASSERT( std::string( cmd.getCMSCorrelationId() ) == 
                            "ID:1234567" );
            CPPUNIT_ASSERT( cmd.getCMSDeliveryMode() == 
                            cms::DeliveryMode::PERSISTANT );
            cmd.setCMSDeliveryMode( cms::DeliveryMode::NON_PERSISTANT );
            CPPUNIT_ASSERT( cmd.getCMSDeliveryMode() == 
                            cms::DeliveryMode::NON_PERSISTANT );
            cmd.setCMSDestination( &topic );
            CPPUNIT_ASSERT( cmd.getCMSDestination()->toString() == 
                            "testTopic" );
            CPPUNIT_ASSERT( cmd.getCMSExpiration() == 0 );
            cmd.setCMSExpiration( 123 );
            CPPUNIT_ASSERT( cmd.getCMSExpiration() == 123 );
            CPPUNIT_ASSERT( cmd.getCMSMessageId() == NULL );
            cmd.setCMSMessageId( "ID:1234567" );
            CPPUNIT_ASSERT( std::string( cmd.getCMSMessageId() ) == 
                            "ID:1234567" );
            CPPUNIT_ASSERT( cmd.getCMSPriority() == 0 );
            cmd.setCMSPriority( 5 );
            CPPUNIT_ASSERT( cmd.getCMSPriority() == 5 );
            CPPUNIT_ASSERT( cmd.getCMSRedelivered() == false );
            cmd.setCMSRedelivered( true );
            CPPUNIT_ASSERT( cmd.getCMSRedelivered() == true );
            CPPUNIT_ASSERT( cmd.getCMSReplyTo() == NULL );
            cmd.setCMSReplyTo( "topic" );
            CPPUNIT_ASSERT( std::string( cmd.getCMSReplyTo() ) == 
                            "topic" );
            CPPUNIT_ASSERT( cmd.getCMSTimeStamp() == 0 );
            cmd.setCMSTimeStamp( 123 );
            CPPUNIT_ASSERT( cmd.getCMSTimeStamp() == 123 );
            CPPUNIT_ASSERT( cmd.getCMSMessageType() == NULL );
            cmd.setCMSMessageType( "test" );
            CPPUNIT_ASSERT( std::string( cmd.getCMSMessageType() ) == 
                            "test" );
            CPPUNIT_ASSERT( cmd.getRedeliveryCount() == 0 );
            cmd.setRedeliveryCount( 123 );
            CPPUNIT_ASSERT( cmd.getRedeliveryCount() == 123 );
            
            const char* bodyBytes = "TESTBODYBYTES\0";
            CPPUNIT_ASSERT( cmd.getBodyLength() == 0 );
            cmd.setBodyBytes( (const unsigned char*)bodyBytes, 
                              strlen( bodyBytes ) + 1 );
            CPPUNIT_ASSERT( cmd.getBodyLength() == 
                            (unsigned int)strlen( bodyBytes ) + 1 );
            CPPUNIT_ASSERT( std::string( (const char*)cmd.getBodyBytes() ) == 
                            bodyBytes );

            cms::Message* cmd2 = cmd.clone();
            
            CPPUNIT_ASSERT( cmd.getCMSPriority() == cmd2->getCMSPriority() );
            CPPUNIT_ASSERT( cmd.getCMSTimeStamp() == cmd2->getCMSTimeStamp() );
            CPPUNIT_ASSERT( cmd.getCMSExpiration() == cmd2->getCMSExpiration() );
            CPPUNIT_ASSERT( cmd.getCMSDeliveryMode() == cmd2->getCMSDeliveryMode() );
            CPPUNIT_ASSERT( std::string(cmd.getCMSCorrelationId()) == cmd2->getCMSCorrelationId() );
            CPPUNIT_ASSERT( std::string(cmd.getCMSReplyTo()) == cmd2->getCMSReplyTo() );
            CPPUNIT_ASSERT( std::string(cmd.getCMSMessageType()) == cmd2->getCMSMessageType() );
            CPPUNIT_ASSERT( std::string(cmd.getCMSMessageId()) == cmd2->getCMSMessageId() );

            core::ActiveMQMessage* message = 
                dynamic_cast< core::ActiveMQMessage* >( cmd2 );
                
            CPPUNIT_ASSERT( message != NULL );
            CPPUNIT_ASSERT( cmd.getRedeliveryCount() == 
                            message->getRedeliveryCount() );
            
            StompCommand* cmd4 = 
                dynamic_cast< StompCommand* >( cmd2 );

            CPPUNIT_ASSERT( cmd4 != NULL );
            CPPUNIT_ASSERT( cmd.getCommandId() == cmd4->getCommandId() );
            CPPUNIT_ASSERT( cmd.getStompCommandId() == cmd4->getStompCommandId() );
            CPPUNIT_ASSERT( cmd.isResponseRequired() == cmd4->isResponseRequired() );
            CPPUNIT_ASSERT( cmd.getCorrelationId() == cmd4->getCorrelationId() );
            CPPUNIT_ASSERT( std::string(cmd.getTransactionId()) == 
                            cmd4->getTransactionId() );

            BytesMessageCommand* cmd5 = 
                dynamic_cast< BytesMessageCommand* >( cmd2 );

            CPPUNIT_ASSERT( cmd5 != NULL );
            CPPUNIT_ASSERT( std::string( (const char*)cmd.getBodyBytes() ) == 
                            (const char*)cmd5->getBodyBytes() );

            delete cmd2;
        }

    };

}}}}

#endif /*_ACTIVEMQ_CONNECTOR_STOMP_COMMANDS_BYTESMESSAGECOMMANDTEST_H_*/
