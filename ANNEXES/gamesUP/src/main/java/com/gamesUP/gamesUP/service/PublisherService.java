package com.gamesUP.gamesUP.service;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ResponseStatusException;

import com.gamesUP.gamesUP.model.Publisher;
import com.gamesUP.gamesUP.repository.PublisherRepository;

@Service
@Transactional
public class PublisherService {

    private final PublisherRepository publisherRepository;

    public PublisherService(PublisherRepository publisherRepository) {
        this.publisherRepository = publisherRepository;
    }

    @Transactional(readOnly = true)
    public List<Publisher> findAll() {
        return publisherRepository.findAll(Sort.by("name"));
    }

    public Publisher create(Publisher publisher) {
        validate(publisher);
        return publisherRepository.save(publisher);
    }

    public Publisher update(Long id, Publisher publisher) {
        Publisher existing = publisherRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Éditeur introuvable"));
        validate(publisher);
        existing.setName(publisher.getName());
        existing.setCountry(publisher.getCountry());
        existing.setWebsite(publisher.getWebsite());
        return publisherRepository.save(existing);
    }

    public void delete(Long id) {
        if (!publisherRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Éditeur introuvable");
        }
        publisherRepository.deleteById(id);
    }

    private void validate(Publisher publisher) {
        if (publisher == null || !StringUtils.hasText(publisher.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Le nom de l'éditeur est requis");
        }
        publisher.setName(publisher.getName().trim());
    }
}
