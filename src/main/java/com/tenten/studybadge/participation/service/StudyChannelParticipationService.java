package com.tenten.studybadge.participation.service;

import com.tenten.studybadge.common.exception.participation.AlreadyAppliedParticipationException;
import com.tenten.studybadge.common.exception.participation.NotFoundParticipationException;
import com.tenten.studybadge.common.exception.participation.OtherMemberParticipationCancelException;
import com.tenten.studybadge.common.exception.studychannel.NotFoundStudyChannelException;
import com.tenten.studybadge.common.exception.studychannel.AlreadyStudyMemberException;
import com.tenten.studybadge.common.exception.studychannel.RecruitmentCompletedStudyChannelException;
import com.tenten.studybadge.participation.domain.entity.Participation;
import com.tenten.studybadge.participation.domain.repository.ParticipationRepository;
import com.tenten.studybadge.study.channel.domain.entity.StudyChannel;
import com.tenten.studybadge.study.channel.domain.repository.StudyChannelRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class StudyChannelParticipationService {

    private final ParticipationRepository  participationRepository;
    private final StudyChannelRepository studyChannelRepository;

    // TODO 1) 탈퇴 당한 회원인가?
    //      2) 참가 거절 당한 회원인가?
    @Transactional
    public void apply(Long studyChannelId, Long memberId) {

        // TODO 추후 존재하는 회원인지 검증하기 (memberId)
        StudyChannel studyChannel = studyChannelRepository.findById(studyChannelId).orElseThrow(NotFoundStudyChannelException::new);

        if (studyChannel.isStudyMember(memberId)) {
            throw new AlreadyStudyMemberException();
        }

        if (studyChannel.isRecruitmentCompleted()) {
            throw new RecruitmentCompletedStudyChannelException();
        }

        if (participationRepository.existsByMemberIdAndStudyChannelId(memberId, studyChannel.getId())) {
            throw new AlreadyAppliedParticipationException();
        }

        Participation participation = Participation.create(memberId, studyChannel);

        participationRepository.save(participation);

    }

    // TODO 이미 승인/거절된 참가 신청을 취소할 경우 예외 처리
    @Transactional
    public void cancel(Long participationId, Long memberId) {
        // TODO 존재하는 회원 여부 검증
        Participation participation = participationRepository.findById(participationId).orElseThrow(NotFoundParticipationException::new);
        if (!participation.isCreatedBy(memberId)) {
            throw new OtherMemberParticipationCancelException();
        }
        participation.cancel();
    }

}
